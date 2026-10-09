package com.diegobergonsi.wakedeck.widget.config

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.widget.WidgetConfigStore
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.widget.WidgetDraft
import com.diegobergonsi.wakedeck.widget.WidgetUpdater
import com.diegobergonsi.wakedeck.widget.work.StatusScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configStore: WidgetConfigStore,
    repository: DeviceRepository,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var widgetId = -1
    private var started = false

    var config by mutableStateOf(WidgetConfig())
        private set
    var loaded by mutableStateOf(false)
        private set

    fun start(id: Int) {
        if (started) return
        started = true
        widgetId = id
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

    val canSave: Boolean get() = config.deviceIds.isNotEmpty()

    fun save(onDone: () -> Unit) {
        if (!canSave) return
        viewModelScope.launch {
            configStore.save(widgetId, config)
            WidgetDraft.clear(widgetId)
            StatusScheduler.ensurePeriodic(context)
            StatusScheduler.refreshNow(context)
            WidgetUpdater.updateAll(context)
            onDone()
        }
    }

    override fun onCleared() {
        if (widgetId >= 0) WidgetDraft.clear(widgetId)
    }
}
