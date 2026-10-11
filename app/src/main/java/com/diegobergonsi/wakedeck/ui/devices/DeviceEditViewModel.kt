package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import androidx.annotation.StringRes
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.GroupRepository
import com.diegobergonsi.wakedeck.domain.BroadcastAddress
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.Group
import com.diegobergonsi.wakedeck.ssh.FingerprintResult
import com.diegobergonsi.wakedeck.ssh.SshShutdown
import com.diegobergonsi.wakedeck.ssh.ShutdownResult
import com.diegobergonsi.wakedeck.domain.MagicPacket
import com.diegobergonsi.wakedeck.domain.OsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FormError { NAME, MAC, BROADCAST, PORT, STATUS_PORT, SSH_USER, SSH_PORT }

data class DeviceForm(
    val name: String = "",
    val mac: String = "",
    val host: String = "",
    val broadcast: String = Device.DEFAULT_BROADCAST,
    val port: String = Device.DEFAULT_PORT.toString(),
    val os: OsType = OsType.LINUX,
    val statusPort: String = "",
    val showInLauncher: Boolean = false,
    val groupId: Long? = null,
    val sshUser: String = "",
    val sshPort: String = Device.DEFAULT_SSH_PORT.toString(),
    val errors: Set<FormError> = emptySet(),
)

const val NEW_DEVICE_ID = -1L

/** Where the "shut down now" flow is. Each state maps to one dialog. */
sealed interface ShutdownUi {
    data object Idle : ShutdownUi
    data object Confirm : ShutdownUi
    data object Busy : ShutdownUi
    data class TrustKey(val fingerprint: String) : ShutdownUi
    data class KeyChanged(val old: String, val seen: String) : ShutdownUi
    data class Message(@StringRes val res: Int, val args: List<Any> = emptyList()) : ShutdownUi
}

@HiltViewModel
class DeviceEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DeviceRepository,
    private val ssh: SshShutdown,
    private val groupRepository: GroupRepository,
) : ViewModel() {
    val groups: StateFlow<List<Group>> = groupRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: NEW_DEVICE_ID
    private var sshHostKey: String = ""

    val isNew: Boolean get() = deviceId == NEW_DEVICE_ID

    var form by mutableStateOf(DeviceForm())
        private set
    var finished by mutableStateOf(false)
        private set

    /** False until an existing device has been read from the database. */
    var loaded by mutableStateOf(isNew)
        private set
    var shutdownUi by mutableStateOf<ShutdownUi>(ShutdownUi.Idle)
        private set

    init {
        if (!isNew) {
            viewModelScope.launch {
                val device = repository.get(deviceId)
                if (device == null) {
                    finished = true
                } else {
                    sshHostKey = device.sshHostKey
                    form = DeviceForm(
                        name = device.name,
                        mac = device.mac,
                        host = device.host,
                        broadcast = device.broadcastAddress,
                        port = device.port.toString(),
                        os = device.os,
                        statusPort = device.statusPort?.toString().orEmpty(),
                        showInLauncher = device.showInLauncher,
                        groupId = device.groupId,
                        sshUser = device.sshUser,
                        sshPort = device.sshPort.toString(),
                    )
                    loaded = true
                }
            }
        }
    }

    fun update(transform: (DeviceForm) -> DeviceForm) {
        form = transform(form).copy(errors = emptySet())
    }

    /** Checks the form. Returns the device to save, or null after marking the errors. */
    private fun validatedDevice(): Device? {
        val f = form
        val errors = buildSet {
            if (f.name.isBlank()) add(FormError.NAME)
            if (MagicPacket.parseMac(f.mac) == null) add(FormError.MAC)
            if (!BroadcastAddress.isValid(f.broadcast)) add(FormError.BROADCAST)
            if (f.port.toIntOrNull() !in 1..65535) add(FormError.PORT)
            if (f.statusPort.isNotBlank() && f.statusPort.toIntOrNull() !in 1..65535) add(FormError.STATUS_PORT)
            if (f.sshUser.any { it.isWhitespace() }) add(FormError.SSH_USER)
            if (f.sshPort.toIntOrNull() !in 1..65535) add(FormError.SSH_PORT)
        }
        if (errors.isNotEmpty()) {
            form = f.copy(errors = errors)
            return null
        }
        return Device(
            id = if (isNew) 0 else deviceId,
            name = f.name.trim(),
            mac = MagicPacket.formatMac(MagicPacket.parseMac(f.mac)!!),
            host = f.host.trim(),
            broadcastAddress = f.broadcast.trim(),
            port = f.port.toInt(),
            os = f.os,
            statusPort = f.statusPort.toIntOrNull(),
            groupId = f.groupId,
            showInLauncher = f.showInLauncher,
            sshUser = f.sshUser.trim(),
            sshPort = f.sshPort.toInt(),
            sshHostKey = sshHostKey,
        )
    }

    /** Creates a group from the edit screen and selects it for this device. */
    fun createGroup(name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        viewModelScope.launch {
            val existing = groupRepository.getAll().firstOrNull { it.name.equals(clean, ignoreCase = true) }
            val id = existing?.id ?: groupRepository.save(Group(name = clean))
            update { it.copy(groupId = id) }
        }
    }

    fun save() {
        val device = validatedDevice() ?: return
        viewModelScope.launch {
            repository.save(device)
            finished = true
        }
    }

    // ---- Remote shutdown over SSH ----

    val canShutDown: Boolean get() = !isNew && form.sshUser.isNotBlank() && form.host.isNotBlank()

    fun requestShutdown() {
        if (canShutDown && validatedDevice() != null) shutdownUi = ShutdownUi.Confirm
    }

    fun dismissShutdown() {
        shutdownUi = ShutdownUi.Idle
    }

    fun confirmShutdown() {
        val device = validatedDevice() ?: return
        shutdownUi = ShutdownUi.Busy
        viewModelScope.launch {
            repository.save(device)
            if (sshHostKey.isBlank()) askToTrustHostKey(device) else runShutdown(device)
        }
    }

    private suspend fun askToTrustHostKey(device: Device) {
        shutdownUi = when (val r = ssh.fetchHostKeyFingerprint(device)) {
            is FingerprintResult.Found -> ShutdownUi.TrustKey(r.fingerprint)
            is FingerprintResult.Failed -> ShutdownUi.Message(R.string.shutdown_unreachable, listOf(r.reason))
        }
    }

    /** The user confirmed the fingerprint (first use) or accepted a changed key. */
    fun trustHostKey() {
        val fingerprint = when (val ui = shutdownUi) {
            is ShutdownUi.TrustKey -> ui.fingerprint
            is ShutdownUi.KeyChanged -> ui.seen
            else -> return
        }
        val rePin = shutdownUi is ShutdownUi.KeyChanged
        sshHostKey = fingerprint
        val device = validatedDevice() ?: return
        shutdownUi = ShutdownUi.Busy
        viewModelScope.launch {
            repository.save(device)
            // A changed key is re-pinned only: the user confirms the shutdown again with the new key in place.
            if (rePin) shutdownUi = ShutdownUi.Confirm else runShutdown(device)
        }
    }

    private suspend fun runShutdown(device: Device) {
        shutdownUi = when (val r = ssh.shutdown(device)) {
            ShutdownResult.Sent -> ShutdownUi.Message(R.string.shutdown_sent, listOf(device.name))
            ShutdownResult.AuthFailed -> ShutdownUi.Message(R.string.shutdown_auth_failed)
            ShutdownResult.NotConfigured -> ShutdownUi.Message(R.string.shutdown_not_configured)
            ShutdownResult.NoHostKey -> ShutdownUi.Message(R.string.shutdown_not_configured)
            is ShutdownResult.HostKeyChanged -> ShutdownUi.KeyChanged(r.expected, r.seen)
            is ShutdownResult.CommandFailed ->
                ShutdownUi.Message(R.string.shutdown_failed, listOf(r.exitStatus, r.message.ifBlank { "-" }))
            is ShutdownResult.Unreachable -> ShutdownUi.Message(R.string.shutdown_unreachable, listOf(r.reason))
        }
    }

    fun delete() {
        if (isNew) return
        viewModelScope.launch {
            repository.delete(deviceId)
            finished = true
        }
    }
}
