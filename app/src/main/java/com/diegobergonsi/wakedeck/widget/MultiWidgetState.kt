package com.diegobergonsi.wakedeck.widget

import android.content.Context
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

data class DeviceRowState(
    val device: Device,
    val status: DeviceStatus,
    val checkedAtMs: Long?,
    val feedback: WakeFeedback,
)

/** State for the list and grid widgets: one row per configured device that still exists. */
data class MultiWidgetState(val config: WidgetConfig?, val rows: List<DeviceRowState>)

object MultiWidgetStates {
    suspend fun load(context: Context, widgetId: Int): MultiWidgetState = flow(context, widgetId).first()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun flow(context: Context, widgetId: Int): Flow<MultiWidgetState> {
        val entry = context.widgetEntryPoint()
        val configs = combine(WidgetDraft.flow(widgetId), entry.configStore().flow(widgetId)) { draft, stored -> draft ?: stored }
        return configs.flatMapLatest { config ->
            val ids = config?.deviceIds.orEmpty()
            if (ids.isEmpty()) {
                flowOf(MultiWidgetState(config, emptyList()))
            } else {
                val rowFlows = ids.map { id ->
                    combine(
                        entry.deviceRepository().observe(id),
                        entry.statusStore().statusFlow(id),
                        entry.statusStore().wakeFlow(id),
                    ) { device, snapshot, wake ->
                        device?.let {
                            DeviceRowState(
                                it,
                                snapshot?.effective(System.currentTimeMillis()) ?: DeviceStatus.CHECKING,
                                snapshot?.checkedAtMs,
                                wake?.feedbackAt(System.currentTimeMillis()) ?: WakeFeedback.NONE,
                            )
                        }
                    }
                }
                combine(rowFlows) { rows -> MultiWidgetState(config, rows.filterNotNull()) }
            }
        }.map { it }
    }
}
