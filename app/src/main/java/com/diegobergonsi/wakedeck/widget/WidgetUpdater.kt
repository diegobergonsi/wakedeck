package com.diegobergonsi.wakedeck.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

/** Re-renders every widget of every type. Add new widget types here. */
object WidgetUpdater {
    suspend fun updateAll(context: Context) {
        SingleDeviceWidget().updateAll(context)
    }
}
