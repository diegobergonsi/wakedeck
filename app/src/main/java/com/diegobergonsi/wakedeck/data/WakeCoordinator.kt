package com.diegobergonsi.wakedeck.data

import android.content.Context
import com.diegobergonsi.wakedeck.data.widget.StatusStore
import com.diegobergonsi.wakedeck.di.ApplicationScope
import com.diegobergonsi.wakedeck.domain.WakeOnLan
import com.diegobergonsi.wakedeck.widget.WakeRecord
import com.diegobergonsi.wakedeck.widget.WidgetUpdater
import com.diegobergonsi.wakedeck.widget.work.StatusScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class WakeOutcome { SENT, FAILED, NOT_FOUND }

data class WakeResult(val outcome: WakeOutcome, val deviceName: String?)

/**
 * One place for "wake a device from outside the main screen" (widgets, launcher shortcuts, Quick Settings tiles):
 * send the packet, record the result so widgets show it, and start the fast status checks.
 */
@Singleton
class WakeCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val devices: DeviceRepository,
    private val wakeOnLan: WakeOnLan,
    private val statuses: StatusStore,
    @ApplicationScope private val scope: CoroutineScope,
) {
    suspend fun wake(deviceId: Long): WakeResult {
        val device = devices.get(deviceId) ?: return WakeResult(WakeOutcome.NOT_FOUND, null)
        val ok = wakeOnLan.send(device).isSuccess
        statuses.recordWake(deviceId, ok)
        WidgetUpdater.updateAll(context)
        if (ok) StatusScheduler.startBurst(context, deviceId)
        return WakeResult(if (ok) WakeOutcome.SENT else WakeOutcome.FAILED, device.name)
    }

    suspend fun clearFeedback(deviceId: Long) {
        statuses.clearWake(deviceId)
        WidgetUpdater.updateAll(context)
    }

    /** Clears the widgets' "Sent" state after its display time, even if the caller has already finished. */
    fun clearFeedbackLater(deviceId: Long) {
        scope.launch {
            delay(WakeRecord.FEEDBACK_MS)
            clearFeedback(deviceId)
        }
    }
}
