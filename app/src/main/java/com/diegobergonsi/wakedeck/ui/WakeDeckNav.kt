package com.diegobergonsi.wakedeck.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diegobergonsi.wakedeck.ui.devices.DeviceEditScreen
import com.diegobergonsi.wakedeck.ui.devices.DeviceListScreen
import com.diegobergonsi.wakedeck.ui.devices.NEW_DEVICE_ID
import com.diegobergonsi.wakedeck.ui.ssh.SshKeyScreen

private const val LIST = "devices"
private const val EDIT = "edit/{deviceId}"
private const val SSH_KEY = "sshkey"

@Composable
fun WakeDeckNav(openAddDevice: Boolean = false, onAddDeviceHandled: () -> Unit = {}) {
    val nav = rememberNavController()
    LaunchedEffect(openAddDevice) {
        if (openAddDevice) {
            nav.navigate("edit/$NEW_DEVICE_ID")
            onAddDeviceHandled()
        }
    }
    NavHost(nav, startDestination = LIST) {
        composable(LIST) {
            DeviceListScreen(
                onAdd = { nav.navigate("edit/$NEW_DEVICE_ID") },
                onEdit = { id -> nav.navigate("edit/$id") },
                onOpenSshKey = { nav.navigate(SSH_KEY) },
            )
        }
        composable(SSH_KEY) { SshKeyScreen(onBack = { nav.popBackStack() }) }
        composable(EDIT, arguments = listOf(navArgument("deviceId") { type = NavType.LongType })) {
            DeviceEditScreen(onDone = { nav.popBackStack() })
        }
    }
}
