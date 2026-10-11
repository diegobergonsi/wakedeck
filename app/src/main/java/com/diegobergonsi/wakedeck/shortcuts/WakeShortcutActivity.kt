package com.diegobergonsi.wakedeck.shortcuts

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.Toast
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.WakeCoordinator
import com.diegobergonsi.wakedeck.data.WakeOutcome
import com.diegobergonsi.wakedeck.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Invisible activity behind the launcher shortcuts: wakes the device, shows a toast and closes at once. */
@AndroidEntryPoint
class WakeShortcutActivity : ComponentActivity() {
    @Inject lateinit var coordinator: WakeCoordinator

    @Inject @ApplicationScope lateinit var scope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceId = intent?.getLongExtra(EXTRA_DEVICE_ID, -1L) ?: -1L
        val appContext = applicationContext
        scope.launch {
            val result = coordinator.wake(deviceId)
            val message = when (result.outcome) {
                WakeOutcome.SENT -> appContext.getString(R.string.wake_sent, result.deviceName)
                WakeOutcome.FAILED -> appContext.getString(R.string.wake_failed, result.deviceName)
                WakeOutcome.NOT_FOUND -> appContext.getString(R.string.widget_device_missing)
            }
            withContext(Dispatchers.Main) { Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show() }
            if (result.outcome != WakeOutcome.NOT_FOUND) coordinator.clearFeedbackLater(deviceId)
        }
        finish()
    }

    companion object {
        const val ACTION_WAKE = "com.diegobergonsi.wakedeck.action.WAKE"
        const val EXTRA_DEVICE_ID = "deviceId"
    }
}
