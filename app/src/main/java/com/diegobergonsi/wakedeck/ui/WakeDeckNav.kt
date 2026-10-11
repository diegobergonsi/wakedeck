package com.diegobergonsi.wakedeck.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diegobergonsi.wakedeck.ui.devices.DeviceEditScreen
import com.diegobergonsi.wakedeck.ui.devices.DeviceListScreen
import com.diegobergonsi.wakedeck.ui.devices.NEW_DEVICE_ID
import com.diegobergonsi.wakedeck.ui.groups.GroupsScreen
import com.diegobergonsi.wakedeck.ui.help.HelpScreen
import com.diegobergonsi.wakedeck.ui.licenses.LicensesScreen
import com.diegobergonsi.wakedeck.ui.ssh.SshKeyScreen

private const val LIST = "devices"
private const val EDIT = "edit/{deviceId}"
private const val SSH_KEY = "sshkey"
private const val GROUPS = "groups"
private const val LICENSES = "licenses"
private const val HELP = "help?section={section}"

@Composable
fun WakeDeckNav(openAddDevice: Boolean = false, onAddDeviceHandled: () -> Unit = {}) {
    val nav = rememberNavController()
    LaunchedEffect(openAddDevice) {
        if (openAddDevice) {
            nav.navigate("edit/$NEW_DEVICE_ID")
            onAddDeviceHandled()
        }
    }
    // The scroll areas span the whole screen; each screen keeps its content in a centred column with [contentSidePadding].
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.TopCenter) {
    NavHost(
        nav,
        modifier = Modifier.fillMaxSize(),
        startDestination = LIST,
        enterTransition = { slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it / 8 } + fadeOut(tween(150)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it / 8 } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it / 4 } + fadeOut(tween(300)) },
    ) {
        composable(LIST) {
            DeviceListScreen(
                onAdd = { nav.navigate("edit/$NEW_DEVICE_ID") },
                onEdit = { id -> nav.navigate("edit/$id") },
                onOpenSshKey = { nav.navigate(SSH_KEY) },
                onOpenGroups = { nav.navigate(GROUPS) },
                onOpenHelp = { nav.navigate("help?section=wol") },
                onOpenLicenses = { nav.navigate(LICENSES) },
            )
        }
        composable(GROUPS) { GroupsScreen(onBack = { nav.popBackStack() }) }
        composable(
            HELP,
            arguments = listOf(navArgument("section") { type = NavType.StringType; nullable = true; defaultValue = null }),
        ) { entry ->
            HelpScreen(onBack = { nav.popBackStack() }, openSection = entry.arguments?.getString("section"))
        }
        composable(LICENSES) { LicensesScreen(onBack = { nav.popBackStack() }) }
        composable(SSH_KEY) { SshKeyScreen(onBack = { nav.popBackStack() }) }
        composable(EDIT, arguments = listOf(navArgument("deviceId") { type = NavType.LongType })) {
            DeviceEditScreen(onDone = { nav.popBackStack() }, onOpenSshHelp = { nav.navigate("help?section=ssh") })
        }
    }
    }
}

private val MAX_CONTENT_WIDTH = 640.dp

/** Side padding that keeps content at most [MAX_CONTENT_WIDTH] wide while the scrolling area itself spans the screen. */
@Composable
fun contentSidePadding(minimum: Dp = 16.dp): Dp = maxOf(minimum, (LocalConfiguration.current.screenWidthDp.dp - MAX_CONTENT_WIDTH) / 2)
