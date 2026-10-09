package com.diegobergonsi.wakedeck.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.domain.StatusChecker
import com.diegobergonsi.wakedeck.domain.WakeOnLan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WakeEvent {
    data class Sent(val deviceName: String) : WakeEvent
    data class Failed(val deviceName: String) : WakeEvent
}

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    repository: DeviceRepository,
    private val wakeOnLan: WakeOnLan,
    private val statusChecker: StatusChecker,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _statuses = MutableStateFlow<Map<Long, DeviceStatus>>(emptyMap())
    val statuses: StateFlow<Map<Long, DeviceStatus>> = _statuses

    private val eventChannel = Channel<WakeEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun wake(device: Device) {
        viewModelScope.launch {
            val result = wakeOnLan.send(device)
            eventChannel.send(
                if (result.isSuccess) WakeEvent.Sent(device.name) else WakeEvent.Failed(device.name),
            )
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
    }
}
