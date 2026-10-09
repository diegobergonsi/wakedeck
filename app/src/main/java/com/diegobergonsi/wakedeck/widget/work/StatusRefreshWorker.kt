package com.diegobergonsi.wakedeck.widget.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.widget.StatusStore
import com.diegobergonsi.wakedeck.data.widget.WidgetConfigStore
import com.diegobergonsi.wakedeck.domain.StatusChecker
import com.diegobergonsi.wakedeck.widget.WidgetUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/** Checks every device used by a widget and refreshes the widgets. */
@HiltWorker
class StatusRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val devices: DeviceRepository,
    private val checker: StatusChecker,
    private val configs: WidgetConfigStore,
    private val statuses: StatusStore,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        configs.pruneExcept(StatusScheduler.activeWidgetIds(applicationContext))
        val targets = configs.allDeviceIds().mapNotNull { devices.get(it) }
        coroutineScope {
            targets.map { device -> async { statuses.setStatus(device.id, checker.check(device)) } }.awaitAll()
        }
        WidgetUpdater.updateAll(applicationContext)
        return Result.success()
    }
}
