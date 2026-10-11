package com.diegobergonsi.wakedeck.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.WakeOutcome
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A Quick Settings tile that wakes one device. Which device is chosen in the tile's settings screen
 * (long-press the tile). The tile is lit while the device is online.
 */
abstract class WakeTileService(private val slot: Int) : TileService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    override fun onStartListening() {
        job?.cancel()
        job = scope.launch { refresh() }
    }

    override fun onStopListening() {
        job?.cancel()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onClick() {
        job?.cancel()
        job = scope.launch { handleClick() }
    }

    private suspend fun refresh() {
        val entry = applicationContext.widgetEntryPoint()
        val device = entry.tileStore().deviceId(slot)?.let { entry.deviceRepository().get(it) }
        if (device == null) {
            render(getString(R.string.tile_choose), null, active = false)
            return
        }
        render(device.name, getString(R.string.status_checking), active = false)
        val status = entry.statusChecker().check(device)
        entry.statusStore().setStatus(device.id, status)
        render(device.name, statusText(status), TileUi.isActive(status))
    }

    private suspend fun handleClick() {
        val entry = applicationContext.widgetEntryPoint()
        val id = entry.tileStore().deviceId(slot)
        val device = id?.let { entry.deviceRepository().get(it) }
        if (device == null) {
            openSettings()
            return
        }
        render(device.name, getString(R.string.widget_sent), active = false)
        val coordinator = entry.wakeCoordinator()
        val result = coordinator.wake(device.id)
        if (result.outcome == WakeOutcome.FAILED) render(device.name, getString(R.string.widget_failed), active = false)
        coordinator.clearFeedbackLater(device.id)
        delay(FEEDBACK_MS)
        refresh()
    }

    private fun statusText(status: DeviceStatus): String = getString(
        when (status) {
            DeviceStatus.ONLINE -> R.string.status_online
            DeviceStatus.OFFLINE -> R.string.status_offline
            DeviceStatus.CHECKING -> R.string.status_checking
            DeviceStatus.UNAVAILABLE -> R.string.status_no_host
            DeviceStatus.STALE -> R.string.status_stale
        },
    )

    private fun render(label: String, subtitle: String?, active: Boolean) {
        val tile = qsTile ?: return
        val (shownLabel, shownSubtitle) = TileUi.texts(label, subtitle, Build.MANUFACTURER)
        tile.label = shownLabel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) tile.subtitle = shownSubtitle
        tile.state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_widget_power)
        tile.updateTile()
    }

    // The Intent overload is only the fallback below API 34, where the PendingIntent one does not exist.
    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun openSettings() {
        val intent = Intent(this, TileConfigActivity::class.java)
            .putExtra(TileConfigActivity.EXTRA_SLOT, slot)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(PendingIntent.getActivity(this, slot, intent, PendingIntent.FLAG_IMMUTABLE))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private companion object {
        const val FEEDBACK_MS = 3_000L
    }
}

class WakeTile1Service : WakeTileService(1)
class WakeTile2Service : WakeTileService(2)
class WakeTile3Service : WakeTileService(3)
