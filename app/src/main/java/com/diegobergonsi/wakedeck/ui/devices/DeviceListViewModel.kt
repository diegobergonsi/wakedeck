package com.diegobergonsi.wakedeck.ui.devices

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.GroupRepository
import com.diegobergonsi.wakedeck.data.WakeCoordinator
import com.diegobergonsi.wakedeck.data.WakeOutcome
import com.diegobergonsi.wakedeck.data.messageRes
import com.diegobergonsi.wakedeck.data.transfer.DeviceTransfer
import com.diegobergonsi.wakedeck.data.transfer.ImportPlan
import com.diegobergonsi.wakedeck.data.transfer.ImportPreview
import com.diegobergonsi.wakedeck.data.transfer.TransferRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.domain.Group
import com.diegobergonsi.wakedeck.domain.StatusChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** A message for the snackbar: a string resource with its arguments. Kept as data so the ViewModel has no UI text. */
data class UiMessage(
    /** A string resource, or a plurals resource when [quantity] is set. */
    val res: Int,
    val args: List<Any> = emptyList(),
    /** When set, [res] is a plurals resource and this number picks the form. */
    val quantity: Int? = null,
)

/** One block on the main screen. [group] is null for devices without a group. */
data class DeviceSection(val group: Group?, val devices: List<Device>)

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: DeviceRepository,
    groups: GroupRepository,
    private val coordinator: WakeCoordinator,
    private val transfer: TransferRepository,
    private val statusChecker: StatusChecker,
) : ViewModel() {
    /** Groups by name, then devices without a group. With no groups at all there is one section without a header. */
    val sections: StateFlow<List<DeviceSection>> = combine(repository.observeAll(), groups.observeAll()) { all, groupList ->
        val named = groupList.mapNotNull { g -> all.filter { it.groupId == g.id }.takeIf { it.isNotEmpty() }?.let { DeviceSection(g, it) } }
        val knownIds = groupList.map { it.id }.toSet()
        val loose = all.filter { it.groupId == null || it.groupId !in knownIds }
        buildList {
            addAll(named)
            if (loose.isNotEmpty()) add(DeviceSection(null, loose))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _statuses = MutableStateFlow<Map<Long, DeviceStatus>>(emptyMap())
    val statuses: StateFlow<Map<Long, DeviceStatus>> = _statuses

    private val messageChannel = Channel<UiMessage>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()

    fun wake(device: Device) {
        viewModelScope.launch {
            val result = coordinator.wake(device.id)
            messageChannel.send(UiMessage(result.messageRes(), listOf(device.name)))
            if (result.outcome != WakeOutcome.NOT_FOUND) coordinator.clearFeedbackLater(device.id)
        }
    }

    /** Wakes every device of a section. No confirmation, like a single wake. */
    fun wakeAll(section: DeviceSection) {
        viewModelScope.launch {
            val sent = section.devices.count { d ->
                (coordinator.wake(d.id, updateWidgets = false).outcome == WakeOutcome.SENT).also { coordinator.clearFeedbackLater(d.id) }
            }
            coordinator.refreshWidgets()
            messageChannel.send(UiMessage(R.plurals.wake_all_result, listOf(sent, section.devices.size), quantity = section.devices.size))
        }
    }

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                runCatching {
                    val (text, count) = transfer.export()
                    context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) }
                        ?: error("cannot write")
                    UiMessage(R.plurals.export_done, listOf(count), quantity = count)
                }.getOrElse { UiMessage(R.string.export_failed) }
            }
            messageChannel.send(message)
        }
    }

    /** The file the user picked, read and checked but not yet imported. The screen shows it for confirmation. */
    private val _pendingImport = MutableStateFlow<ImportPlan?>(null)
    val pendingImport: StateFlow<ImportPlan?> = _pendingImport

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            val preview = withContext(Dispatchers.IO) {
                runCatching {
                    val text = context.contentResolver.openInputStream(uri)?.use { readLimited(it, MAX_IMPORT_BYTES) }
                        ?: error("cannot read")
                    require(text.length <= MAX_IMPORT_CHARS) { "file too large" }
                    transfer.preview(text)
                }.getOrElse { ImportPreview.Failed("unreadable") }
            }
            when (preview) {
                is ImportPreview.Ready -> _pendingImport.value = preview.plan
                is ImportPreview.Failed -> messageChannel.send(UiMessage(R.string.import_failed))
                ImportPreview.TooMany -> messageChannel.send(UiMessage(R.string.import_too_many, listOf(DeviceTransfer.MAX_DEVICES)))
            }
        }
    }

    fun confirmImport() {
        val plan = _pendingImport.value ?: return
        _pendingImport.value = null
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                runCatching {
                    val done = transfer.commit(plan)
                    UiMessage(R.string.import_done, listOf(done.added, done.skipped, done.invalid))
                }.getOrElse { UiMessage(R.string.import_failed) }
            }
            messageChannel.send(message)
        }
    }

    fun cancelImport() {
        _pendingImport.value = null
    }

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing

    /** Checks all devices every [POLL_INTERVAL_MS] until cancelled. Call while the screen is visible. */
    suspend fun pollStatuses() {
        while (true) {
            checkAll()
            delay(POLL_INTERVAL_MS)
        }
    }

    /** Button or pull-down: one check of every device now, with a progress indicator. */
    fun refresh() {
        if (_refreshing.value) return
        viewModelScope.launch {
            _refreshing.value = true
            try {
                checkAll()
            } finally {
                _refreshing.value = false
            }
        }
    }

    private suspend fun checkAll() {
        val current = repository.observeAll().first()
        _statuses.update { old -> old.filterKeys { id -> current.any { it.id == id } } }
        // A cap on parallel checks: a long list must not open hundreds of connections at once.
        val limit = Semaphore(MAX_PARALLEL_CHECKS)
        coroutineScope {
            current.forEach { device ->
                launch {
                    val status = limit.withPermit { statusChecker.check(device) }
                    _statuses.update { it + (device.id to status) }
                }
            }
        }
    }

    private companion object {
        const val POLL_INTERVAL_MS = 5_000L
        const val MAX_PARALLEL_CHECKS = 8
        const val MAX_IMPORT_CHARS = 1_000_000
        const val MAX_IMPORT_BYTES = 4_000_000
    }
}

/** Reads at most [max] bytes, so a huge file fails before it fills memory. */
private fun readLimited(stream: java.io.InputStream, max: Int): String {
    val out = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    while (true) {
        val n = stream.read(buffer)
        if (n < 0) break
        require(out.size() + n <= max) { "file too large" }
        out.write(buffer, 0, n)
    }
    return out.toByteArray().decodeToString()
}
