package com.diegobergonsi.wakedeck.widget.config

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.widget.WidgetConfigStore
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.di.ApplicationScope
import com.diegobergonsi.wakedeck.widget.WidgetDraft
import com.diegobergonsi.wakedeck.widget.WidgetType
import com.diegobergonsi.wakedeck.widget.WidgetUpdater
import com.diegobergonsi.wakedeck.widget.work.StatusScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configStore: WidgetConfigStore,
    repository: DeviceRepository,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var widgetId = -1
    private var started = false
    private var saved = false

    var config by mutableStateOf(WidgetConfig())
        private set
    var loaded by mutableStateOf(false)
        private set
    var type by mutableStateOf(WidgetType.SINGLE)
        private set

    fun start(id: Int) {
        if (started) return
        started = true
        widgetId = id
        type = WidgetType.fromReceiverClass(AppWidgetManager.getInstance(context).getAppWidgetInfo(id)?.provider?.className)
        viewModelScope.launch {
            configStore.get(id)?.let { config = it }
            WidgetDraft.set(id, config)
            loaded = true
        }
    }

    fun update(transform: (WidgetConfig) -> WidgetConfig) {
        config = transform(config)
        if (widgetId >= 0) WidgetDraft.set(widgetId, config)
    }

    fun selectDevice(id: Long) = update { it.copy(deviceIds = listOf(id)) }

    /** For list and grid widgets: add or remove one device, keeping the order of the device list. */
    fun toggleDevice(id: Long) {
        val chosen = config.deviceIds.toMutableSet().apply { if (!add(id)) remove(id) }
        update { it.copy(deviceIds = devices.value.map { d -> d.id }.filter { it in chosen }) }
    }

    fun selectAllDevices() = update { it.copy(deviceIds = devices.value.map { d -> d.id }) }

    val canSave: Boolean get() = config.deviceIds.isNotEmpty()

    fun save(onDone: () -> Unit) {
        if (!canSave) return
        viewModelScope.launch {
            configStore.save(widgetId, config)
            saved = true
            WidgetDraft.clear(widgetId)
            StatusScheduler.ensurePeriodic(context)
            StatusScheduler.refreshNow(context)
            WidgetUpdater.updateAll(context)
            onDone()
        }
    }

    override fun onCleared() {
        if (widgetId < 0) return
        WidgetDraft.clear(widgetId)
        // Cancelled: the home-screen widget may still show the unsaved look, so redraw it from the stored config.
        if (!saved) appScope.launch { WidgetUpdater.updateAll(context) }
    }
}
