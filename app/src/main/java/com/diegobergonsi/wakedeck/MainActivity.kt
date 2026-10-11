package com.diegobergonsi.wakedeck

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.diegobergonsi.wakedeck.ui.WakeDeckNav
import com.diegobergonsi.wakedeck.ui.theme.WakeDeckTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var openAddDevice by mutableStateOf(false)
    private var showNotificationInfo by mutableStateOf(false)

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* nothing to do either way */ }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openAddDevice = intent.action == ACTION_ADD_DEVICE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openAddDevice = intent?.action == ACTION_ADD_DEVICE
        askForNotificationsOnce()
        enableEdgeToEdge()
        setContent {
            WakeDeckTheme {
                WakeDeckNav(openAddDevice, onAddDeviceHandled = { openAddDevice = false })
                if (showNotificationInfo) {
                    AlertDialog(
                        onDismissRequest = { showNotificationInfo = false },
                        title = { Text(stringResource(R.string.notif_info_title)) },
                        text = { Text(stringResource(R.string.notif_info_text)) },
                        confirmButton = {
                            TextButton(onClick = {
                                showNotificationInfo = false
                                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }) { Text(stringResource(R.string.notif_info_continue)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNotificationInfo = false }) { Text(stringResource(R.string.notif_info_not_now)) }
                        },
                    )
                }
            }
        }
    }

    /** Asks once, on the first launch where it is needed. If refused, the user can still allow it in system settings. */
    private fun askForNotificationsOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (granted || prefs.getBoolean("asked_notifications", false)) return
        prefs.edit().putBoolean("asked_notifications", true).apply()
        showNotificationInfo = true
    }

    companion object {
        const val ACTION_ADD_DEVICE = "com.diegobergonsi.wakedeck.action.ADD_DEVICE"
    }
}
