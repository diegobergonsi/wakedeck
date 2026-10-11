package com.diegobergonsi.wakedeck.ui.devices

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.GroupRepository
import com.diegobergonsi.wakedeck.data.WakeCoordinator
import com.diegobergonsi.wakedeck.data.WakeOutcome
import com.diegobergonsi.wakedeck.data.transfer.ImportOutcome
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** A message for the snackbar: a string resource with its arguments. Kept as data so the ViewModel has no UI text. */
data class UiMessage(@StringRes val res: Int, val args: List<Any> = emptyList())

/** One block on the main screen. [group] is null for devices without a group. */
data class DeviceSection(val group: Group?, val devices: List<Device>)

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    repository: DeviceRepository,
    groups: GroupRepository,
    private val coordinator: WakeCoordinator,
    private val transfer: TransferRepository,
    private val statusChecker: StatusChecker,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
            messageChannel.send(
                UiMessage(if (result.outcome == WakeOutcome.SENT) R.string.wake_sent else R.string.wake_failed, listOf(device.name)),
            )
            if (result.outcome != WakeOutcome.NOT_FOUND) coordinator.clearFeedbackLater(device.id)
        }
    }

    /** Wakes every device of a section. No confirmation, like a single wake. */
    fun wakeAll(section: DeviceSection) {
        viewModelScope.launch {
            val sent = section.devices.count { d ->
                (coordinator.wake(d.id).outcome == WakeOutcome.SENT).also { coordinator.clearFeedbackLater(d.id) }
            }
            messageChannel.send(UiMessage(R.string.wake_all_result, listOf(sent, section.devices.size)))
        }
    }

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                runCatching {
                    val (text, count) = transfer.export()
                    context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) }
                        ?: error("cannot write")
                    UiMessage(R.string.export_done, listOf(count))
                }.getOrElse { UiMessage(R.string.export_failed) }
            }
            messageChannel.send(message)
        }
    }

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                runCatching {
                    val text = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                        ?: error("cannot read")
                    require(text.length <= MAX_IMPORT_CHARS) { "file too large" }
                    when (val outcome = transfer.import(text)) {
                        is ImportOutcome.Done -> UiMessage(R.string.import_done, listOf(outcome.added, outcome.skipped, outcome.invalid))
                        is ImportOutcome.Failed -> UiMessage(R.string.import_failed)
                    }
                }.getOrElse { UiMessage(R.string.import_failed) }
            }
            messageChannel.send(message)
        }
    }

    /** Checks all devices every [POLL_INTERVAL_MS] until cancelled. Call while the screen is visible. */
    suspend fun pollStatuses() {
        while (true) {
            val current = devices.value
            _statuses.update { old -> old.filterKeys { id -> current.any { it.id == id } } }
            coroutineScope {
                current.forEach { device ->
                    launch {
                        val status = statusChecker.check(device)
                        _statuses.update { it + (device.id to status) }
                    }
                }
            }
            delay(POLL_INTERVAL_MS)
        }
    }

    private companion object {
        const val POLL_INTERVAL_MS = 5_000L
        const val MAX_IMPORT_CHARS = 1_000_000
    }
}
