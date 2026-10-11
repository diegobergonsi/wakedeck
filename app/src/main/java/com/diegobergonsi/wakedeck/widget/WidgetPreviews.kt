package com.diegobergonsi.wakedeck.widget

import android.content.Context
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.domain.OsType
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import com.diegobergonsi.wakedeck.widget.config.WidgetIcon

/** Sample data for the widget picker previews. Never uses the user's real devices. */
object SampleWidgetStates {
    private fun device(id: Long, name: String) =
        Device(id = id, name = name, mac = "00:00:00:00:00:00", host = "192.168.1.$id", os = OsType.LINUX)

    private val now get() = System.currentTimeMillis()

    fun single() = SingleWidgetState(
        config = WidgetConfig(deviceIds = listOf(1), icon = WidgetIcon.DESKTOP),
        device = device(1, "My PC"),
        status = DeviceStatus.ONLINE,
        feedback = WakeFeedback.NONE,
        checkedAtMs = now,
    )

    /** [count] sample devices (1 to 3). The picker previews are small, so the grid uses 2 and the list 3. */
    fun multi(count: Int = 3) = MultiWidgetState(
        config = WidgetConfig(deviceIds = (1L..count.toLong()).toList(), icon = WidgetIcon.DESKTOP),
        rows = listOf(
            DeviceRowState(device(1, "Desktop"), DeviceStatus.ONLINE, now, WakeFeedback.NONE),
            DeviceRowState(device(2, "Media PC"), DeviceStatus.OFFLINE, now, WakeFeedback.NONE),
            DeviceRowState(device(3, "Server"), DeviceStatus.ONLINE, now, WakeFeedback.NONE),
        ).take(count),
    )
}

object WidgetPreviews {
    private const val PREFS = "widget_previews"
    private const val KEY_PUBLISHED_FOR = "published_for_update_time"
    private const val KEY_LAST_ATTEMPT = "last_attempt_ms"
    private const val RETRY_AFTER_MS = 3 * 60 * 60 * 1000L

    /**
     * Android 15+ lets the app supply the widget picker previews. The system drops them whenever the app is
     * installed or updated, so we publish once per install time (not per version number), and at most once per
     * install because the system rate-limits this call. Older Android versions keep the default picker entry.
     */
    suspend fun publishIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT < 35) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val installedAt = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        if (prefs.getLong(KEY_PUBLISHED_FOR, -1) == installedAt) return
        // The system rate-limits this call and failed attempts may count, so back off instead of retrying every launch.
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(KEY_LAST_ATTEMPT, 0) < RETRY_AFTER_MS) return
        prefs.edit().putLong(KEY_LAST_ATTEMPT, now).apply()
        val manager = GlanceAppWidgetManager(context)
        val results = listOf(
            manager.setWidgetPreviews(SingleDeviceWidgetReceiver::class),
            manager.setWidgetPreviews(DeviceListWidgetReceiver::class),
            manager.setWidgetPreviews(DeviceGridWidgetReceiver::class),
        )
        // If the system rate-limited us, the version stays unpublished and a later launch retries after the back-off.
        if (results.all { it == GlanceAppWidgetManager.SET_WIDGET_PREVIEWS_RESULT_SUCCESS }) {
            prefs.edit().putLong(KEY_PUBLISHED_FOR, installedAt).apply()
        }
    }
}
