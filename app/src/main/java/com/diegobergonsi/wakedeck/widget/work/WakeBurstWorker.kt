package com.diegobergonsi.wakedeck.widget.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.widget.StatusStore
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.domain.StatusChecker
import com.diegobergonsi.wakedeck.widget.WidgetUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

/** Polls one device every few seconds after a wake tap, until it is online or about two minutes passed. */
@HiltWorker
class WakeBurstWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val devices: DeviceRepository,
    private val checker: StatusChecker,
    private val statuses: StatusStore,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val device = devices.get(inputData.getLong(StatusScheduler.KEY_DEVICE_ID, -1)) ?: return Result.success()
        repeat(MAX_CHECKS) {
            val status = checker.check(device)
            statuses.setStatus(device.id, status)
            WidgetUpdater.updateAll(applicationContext)
            if (status == DeviceStatus.ONLINE || status == DeviceStatus.UNAVAILABLE) return Result.success()
            delay(INTERVAL_MS)
        }
        return Result.success()
    }

    private companion object {
        const val MAX_CHECKS = 24
        const val INTERVAL_MS = 5_000L
    }
}
