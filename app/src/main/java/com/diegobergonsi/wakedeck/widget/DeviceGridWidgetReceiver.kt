package com.diegobergonsi.wakedeck.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.diegobergonsi.wakedeck.widget.work.StatusScheduler

class DeviceGridWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DeviceGridWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        StatusScheduler.ensurePeriodic(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        StatusScheduler.cancelPeriodicIfNoWidgets(context)
    }
}
