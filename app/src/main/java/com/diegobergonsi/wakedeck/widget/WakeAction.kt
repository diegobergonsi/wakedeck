package com.diegobergonsi.wakedeck.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.widget.work.StatusScheduler
import kotlinx.coroutines.delay

val DeviceIdKey = ActionParameters.Key<Long>("deviceId")

/** Runs in the background when the in-widget Wake button is tapped. The app never opens. */
class WakeAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val deviceId = parameters[DeviceIdKey] ?: return
        val entry = context.widgetEntryPoint()
        val device = entry.deviceRepository().get(deviceId) ?: return

        val ok = entry.wakeOnLan().send(device).isSuccess
        entry.statusStore().recordWake(deviceId, ok)
        WidgetUpdater.updateAll(context)
        if (ok) StatusScheduler.startBurst(context, deviceId)

        delay(WakeRecord.FEEDBACK_MS)
        entry.statusStore().clearWake(deviceId)
        WidgetUpdater.updateAll(context)
    }
}
