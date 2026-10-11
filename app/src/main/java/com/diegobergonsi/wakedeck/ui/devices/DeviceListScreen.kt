package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.ui.language.LanguageDialog
import com.diegobergonsi.wakedeck.ui.language.perAppLanguageSupported
import com.diegobergonsi.wakedeck.ui.theme.ThemeDialog
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onOpenSshKey: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenHelp: () -> Unit,
    viewModel: DeviceListViewModel = hiltViewModel(),
) {
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    val hasDevices = sections.isNotEmpty()
    var menuOpen by remember { mutableStateOf(false) }
    var languageOpen by remember { mutableStateOf(false) }
    var themeOpen by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let(viewModel::exportTo)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(viewModel::importFrom)
    }
    val statuses by viewModel.statuses.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbar = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.pollStatuses() }
    }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { m ->
            snackbar.currentSnackbarData?.dismiss()
            val args = m.args.toTypedArray()
            val text = if (m.quantity != null) resources.getQuantityString(m.res, m.quantity, *args) else resources.getString(m.res, *args)
            snackbar.showSnackbar(text)
        }
    }

    if (themeOpen) ThemeDialog(onDismiss = { themeOpen = false })
    if (languageOpen && perAppLanguageSupported) {
        @Suppress("NewApi")
        LanguageDialog(onDismiss = { languageOpen = false })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    if (hasDevices) IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.widget_refresh))
                    }
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu_more))
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        MenuItem(R.string.menu_groups) { menuOpen = false; onOpenGroups() }
                        HorizontalDivider()
                        MenuItem(R.string.menu_import) { menuOpen = false; importLauncher.launch(arrayOf("application/json", "text/*", "application/octet-stream")) }
                        MenuItem(R.string.menu_export) { menuOpen = false; exportLauncher.launch("wakedeck-devices.json") }
                        HorizontalDivider()
                        MenuItem(R.string.menu_ssh_key) { menuOpen = false; onOpenSshKey() }
                        MenuItem(R.string.config_theme) { menuOpen = false; themeOpen = true }
                        if (perAppLanguageSupported) MenuItem(R.string.menu_language) { menuOpen = false; languageOpen = true }
                        MenuItem(R.string.menu_help) { menuOpen = false; onOpenHelp() }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd) { Text(stringResource(R.string.add_device)) }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.bg_power_ring),
            contentDescription = null,
            modifier = Modifier.align(if (hasDevices) Alignment.BottomCenter else Alignment.Center).padding(padding).size(if (hasDevices) 300.dp else 340.dp),
            alpha = if (hasDevices) 0.10f else 0.22f,
        )
        if (!hasDevices) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val muted = MaterialTheme.colorScheme.onSurfaceVariant
                Text(stringResource(R.string.empty_devices), style = MaterialTheme.typography.bodyLarge, color = muted)
                Text(stringResource(R.string.empty_devices_prereq), style = MaterialTheme.typography.bodyMedium, color = muted)
                TextButton(onClick = onOpenHelp) { Text(stringResource(R.string.empty_devices_help)) }
                Text(stringResource(R.string.empty_devices_widgets), style = MaterialTheme.typography.bodyMedium, color = muted)
            }
        } else {
            PullToRefreshBox(isRefreshing = refreshing, onRefresh = viewModel::refresh, modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val hasGroups = sections.any { it.group != null }
                sections.forEach { section ->
                    if (section.group != null) {
                        item(key = "group-${section.group.id}") {
                            GroupCard(
                                section,
                                statusOf = { statusOf(it, statuses) },
                                onWake = viewModel::wake,
                                onWakeAll = { viewModel.wakeAll(section) },
                                onEdit = onEdit,
                            )
                        }
                    } else {
                        if (hasGroups) {
                            item(key = "other-label") {
                                Text(
                                    stringResource(R.string.group_other),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                                )
                            }
                        }
                        items(section.devices, key = { it.id }) { device ->
                            DeviceCard(device, statusOf(device, statuses), onWake = { viewModel.wake(device) }, onEdit = { onEdit(device.id) })
                        }
                    }
                }
            }
            }
        }
        }
    }
}

@Composable
private fun DeviceCard(device: Device, status: DeviceStatus, onWake: () -> Unit, onEdit: () -> Unit) {
    Card(Modifier.fillMaxWidth()) { DeviceRowContent(device, status, onWake, onEdit) }
}

/** The device line itself, without a background, so it works both in a card and inside a group container. */
@Composable
private fun DeviceRowContent(device: Device, status: DeviceStatus, onWake: () -> Unit, onEdit: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(device.name, style = MaterialTheme.typography.titleMedium)
            Text(
                device.mac,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(R.string.broadcast_summary, device.broadcastAddress, device.port),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            StatusIndicator(status, Modifier.padding(top = 6.dp))
        }
        Button(onClick = onWake) { Text(stringResource(R.string.wake)) }
    }
}

/** One group as a single container: name and Wake all on top, its devices below, thin lines between them. */
@Composable
private fun GroupCard(
    section: DeviceSection,
    statusOf: (Device) -> DeviceStatus,
    onWake: (Device) -> Unit,
    onWakeAll: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                section.group?.name.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onWakeAll) { Text(stringResource(R.string.wake_all)) }
        }
        section.devices.forEachIndexed { index, device ->
            HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            DeviceRowContent(device, statusOf(device), onWake = { onWake(device) }, onEdit = { onEdit(device.id) })
        }
    }
}

private fun statusOf(device: Device, statuses: Map<Long, DeviceStatus>): DeviceStatus =
    if (device.host.isBlank()) DeviceStatus.UNAVAILABLE else statuses[device.id] ?: DeviceStatus.CHECKING

@Composable
private fun StatusIndicator(status: DeviceStatus, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    val (color, label) = when (status) {
        DeviceStatus.ONLINE -> statusGreen(dark) to R.string.status_online
        DeviceStatus.OFFLINE -> statusRed(dark) to R.string.status_offline
        DeviceStatus.CHECKING -> MaterialTheme.colorScheme.outline to R.string.status_checking
        DeviceStatus.UNAVAILABLE -> MaterialTheme.colorScheme.outline to R.string.status_no_host
        DeviceStatus.STALE -> MaterialTheme.colorScheme.outline to R.string.status_stale
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(
            stringResource(label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Lighter variants on a dark surface keep the dot readable. */
private fun statusGreen(dark: Boolean) = if (dark) Color(0xFF5BD37F) else Color(0xFF2E9E4F)

private fun statusRed(dark: Boolean) = if (dark) Color(0xFFFF7A7A) else Color(0xFFD64545)

@Composable
private fun MenuItem(label: Int, onClick: () -> Unit) {
    DropdownMenuItem(text = { Text(stringResource(label)) }, onClick = onClick)
}

