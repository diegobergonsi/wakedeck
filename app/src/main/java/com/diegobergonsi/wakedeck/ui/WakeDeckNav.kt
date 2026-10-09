package com.diegobergonsi.wakedeck.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diegobergonsi.wakedeck.ui.devices.DeviceEditScreen
import com.diegobergonsi.wakedeck.ui.devices.DeviceListScreen
import com.diegobergonsi.wakedeck.ui.devices.NEW_DEVICE_ID

private const val LIST = "devices"
private const val EDIT = "edit/{deviceId}"

@Composable
fun WakeDeckNav() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = LIST) {
        composable(LIST) {
            DeviceListScreen(
                onAdd = { nav.navigate("edit/$NEW_DEVICE_ID") },
                onEdit = { id -> nav.navigate("edit/$id") },
            )
        }
        composable(EDIT, arguments = listOf(navArgument("deviceId") { type = NavType.LongType })) {
            DeviceEditScreen(onDone = { nav.popBackStack() })
        }
    }
}
