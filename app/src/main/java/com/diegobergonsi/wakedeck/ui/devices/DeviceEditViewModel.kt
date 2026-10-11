package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.MagicPacket
import com.diegobergonsi.wakedeck.domain.OsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FormError { NAME, MAC, BROADCAST, PORT, STATUS_PORT }

data class DeviceForm(
    val name: String = "",
    val mac: String = "",
    val host: String = "",
    val broadcast: String = Device.DEFAULT_BROADCAST,
    val port: String = Device.DEFAULT_PORT.toString(),
    val os: OsType = OsType.LINUX,
    val statusPort: String = "",
    val showInLauncher: Boolean = false,
    val errors: Set<FormError> = emptySet(),
)

const val NEW_DEVICE_ID = -1L

@HiltViewModel
class DeviceEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DeviceRepository,
) : ViewModel() {
    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: NEW_DEVICE_ID
    private var groupId: Long? = null

    val isNew: Boolean get() = deviceId == NEW_DEVICE_ID

    var form by mutableStateOf(DeviceForm())
        private set
    var finished by mutableStateOf(false)
        private set

    init {
        if (!isNew) {
            viewModelScope.launch {
                val device = repository.get(deviceId)
                if (device == null) {
                    finished = true
                } else {
                    groupId = device.groupId
                    form = DeviceForm(
                        name = device.name,
                        mac = device.mac,
                        host = device.host,
                        broadcast = device.broadcastAddress,
                        port = device.port.toString(),
                        os = device.os,
                        statusPort = device.statusPort?.toString().orEmpty(),
                        showInLauncher = device.showInLauncher,
                    )
                }
            }
        }
    }

    fun update(transform: (DeviceForm) -> DeviceForm) {
        form = transform(form).copy(errors = emptySet())
    }

    fun save() {
        val f = form
        val errors = buildSet {
            if (f.name.isBlank()) add(FormError.NAME)
            if (MagicPacket.parseMac(f.mac) == null) add(FormError.MAC)
            if (f.broadcast.isBlank() || f.broadcast.any { it.isWhitespace() }) add(FormError.BROADCAST)
            if (f.port.toIntOrNull() !in 1..65535) add(FormError.PORT)
            if (f.statusPort.isNotBlank() && f.statusPort.toIntOrNull() !in 1..65535) add(FormError.STATUS_PORT)
        }
        if (errors.isNotEmpty()) {
            form = f.copy(errors = errors)
            return
        }
        viewModelScope.launch {
            repository.save(
                Device(
                    id = if (isNew) 0 else deviceId,
                    name = f.name.trim(),
                    mac = MagicPacket.formatMac(MagicPacket.parseMac(f.mac)!!),
                    host = f.host.trim(),
                    broadcastAddress = f.broadcast.trim(),
                    port = f.port.toInt(),
                    os = f.os,
                    statusPort = f.statusPort.toIntOrNull(),
                    groupId = groupId,
                    showInLauncher = f.showInLauncher,
                ),
            )
            finished = true
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
