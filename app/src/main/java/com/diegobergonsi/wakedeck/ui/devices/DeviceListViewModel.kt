package com.diegobergonsi.wakedeck.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.WakeOnLan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
}
