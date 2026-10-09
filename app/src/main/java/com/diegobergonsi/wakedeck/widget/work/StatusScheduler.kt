package com.diegobergonsi.wakedeck.widget.work

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.diegobergonsi.wakedeck.widget.SingleDeviceWidgetReceiver
import java.util.concurrent.TimeUnit

object StatusScheduler {
    private const val PERIODIC = "status-refresh"
    private const val NOW = "status-refresh-now"
    const val KEY_DEVICE_ID = "deviceId"

    /** Receivers of every widget type. Add new ones here. */
    private val receivers = listOf(SingleDeviceWidgetReceiver::class.java)

    fun ensurePeriodic(context: Context) {
        val request = PeriodicWorkRequestBuilder<StatusRefreshWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(PERIODIC, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun refreshNow(context: Context) {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(NOW, ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<StatusRefreshWorker>().build())
    }

    /** Fast checks for about two minutes after a wake tap, so the status turns green soon after boot. */
    fun startBurst(context: Context, deviceId: Long) {
        val request = OneTimeWorkRequestBuilder<WakeBurstWorker>()
            .setInputData(workDataOf(KEY_DEVICE_ID to deviceId))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("status-burst-$deviceId", ExistingWorkPolicy.REPLACE, request)
    }

    fun activeWidgetIds(context: Context): Set<Int> {
        val manager = AppWidgetManager.getInstance(context)
        return receivers.flatMap { manager.getAppWidgetIds(ComponentName(context, it)).toList() }.toSet()
    }

    fun cancelPeriodicIfNoWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val any = receivers.any { manager.getAppWidgetIds(ComponentName(context, it)).isNotEmpty() }
        if (!any) WorkManager.getInstance(context).cancelUniqueWork(PERIODIC)
    }
}
