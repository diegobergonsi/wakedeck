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
import kotlinx.coroutines.delay

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
            val checks = devices.map { d -> async { d.id to entry.statusChecker().check(d) } }
            // A check on a reachable device takes milliseconds. Hold "Checking..." long enough to be seen.
            delay(MIN_CHECKING_MS)
            checks.awaitAll().forEach { (id, status) -> statuses.setStatus(id, status) }
        }
        WidgetUpdater.updateAll(context)
    }

    private companion object {
        const val MIN_CHECKING_MS = 1_000L
    }
}
