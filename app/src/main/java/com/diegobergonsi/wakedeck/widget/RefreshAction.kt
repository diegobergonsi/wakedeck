package com.diegobergonsi.wakedeck.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/** Comma separated device ids, for widgets that show several devices. */
val DeviceIdsKey = ActionParameters.Key<String>("deviceIds")

/** Re-checks one or more devices right now. The widget shows "Checking..." until the results arrive. */
class RefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val ids = parameters[DeviceIdsKey]?.split(',')?.mapNotNull { it.trim().toLongOrNull() }
            ?: listOfNotNull(parameters[DeviceIdKey])
        val entry = context.widgetEntryPoint()
        val devices = ids.mapNotNull { entry.deviceRepository().get(it) }
        val statuses = entry.statusStore()

        // updateAll also restarts the widget's session if Glance closed it while idle.
        devices.forEach { statuses.setStatus(it.id, DeviceStatus.CHECKING) }
        WidgetUpdater.updateAll(context)
        coroutineScope {
            devices.map { d -> async { statuses.setStatus(d.id, entry.statusChecker().check(d)) } }.awaitAll()
        }
        WidgetUpdater.updateAll(context)
    }
}
