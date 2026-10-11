package com.diegobergonsi.wakedeck.tile

import android.content.ComponentName
import android.content.Context
import android.service.quicksettings.TileService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.domain.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TileConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val store: TileStore,
    repository: DeviceRepository,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var selected by mutableStateOf<Long?>(null)
        private set
    private var slot = 0
    private var started = false

    fun start(slot: Int) {
        if (started) return
        started = true
        this.slot = slot
        viewModelScope.launch { selected = store.deviceId(slot) }
    }

    fun select(id: Long?) {
        selected = id
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            store.set(slot, selected)
            val service = when (slot) {
                1 -> WakeTile1Service::class.java
                2 -> WakeTile2Service::class.java
                else -> WakeTile3Service::class.java
            }
            TileService.requestListeningState(context, ComponentName(context, service))
            onDone()
        }
    }
}
