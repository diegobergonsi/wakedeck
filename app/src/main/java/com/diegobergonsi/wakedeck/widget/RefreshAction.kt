package com.diegobergonsi.wakedeck.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.DeviceStatus

/** Re-checks one device right now. The widget shows "Checking..." until the result arrives. */
class RefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val deviceId = parameters[DeviceIdKey] ?: return
        val entry = context.widgetEntryPoint()
        val device = entry.deviceRepository().get(deviceId) ?: return
        val statuses = entry.statusStore()
        // updateAll also restarts the widget's session if Glance closed it while idle.
        statuses.setStatus(deviceId, DeviceStatus.CHECKING)
        WidgetUpdater.updateAll(context)
        statuses.setStatus(deviceId, entry.statusChecker().check(device))
        WidgetUpdater.updateAll(context)
    }
}
