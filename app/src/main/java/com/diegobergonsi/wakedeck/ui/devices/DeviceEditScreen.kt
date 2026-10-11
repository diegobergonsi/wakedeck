package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.Icons
import com.diegobergonsi.wakedeck.domain.Device
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diegobergonsi.wakedeck.ui.groups.NameDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.domain.OsType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DeviceEditScreen(onDone: () -> Unit, onOpenSshHelp: () -> Unit, viewModel: DeviceEditViewModel = hiltViewModel()) {
    val form = viewModel.form
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.finished) { if (viewModel.finished) onDone() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isNew) R.string.add_device else R.string.edit_device))
                },
                navigationIcon = {
                    IconButton(onClick = onDone) { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel)) }
                },
                actions = { TextButton(onClick = viewModel::save) { Text(stringResource(R.string.save)) } },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Field(
                label = R.string.field_name,
                value = form.name,
                error = FormError.NAME in form.errors,
                errorText = R.string.error_name,
            ) { v -> viewModel.update { it.copy(name = v.take(Device.MAX_NAME)) } }
            Field(
                label = R.string.field_mac,
                value = form.mac,
                error = FormError.MAC in form.errors,
                errorText = R.string.error_mac,
            ) { v -> viewModel.update { it.copy(mac = v) } }
            Field(label = R.string.field_host, value = form.host, supporting = R.string.hint_host) { v ->
                viewModel.update { it.copy(host = v.take(Device.MAX_HOST)) }
            }
            Field(
                label = R.string.field_broadcast,
                value = form.broadcast,
                error = FormError.BROADCAST in form.errors,
                errorText = R.string.error_broadcast,
                supporting = R.string.hint_broadcast,
            ) { v -> viewModel.update { it.copy(broadcast = v) } }
            val hasErrors = form.errors.isNotEmpty()
            var moreOpen by rememberSaveable {
                mutableStateOf(
                    !viewModel.isNew && (form.port != Device.DEFAULT_PORT.toString() || form.statusPort.isNotBlank() ||
                        form.groupId != null || form.showInLauncher || form.os != OsType.LINUX),
                )
            }
            var sshOpen by rememberSaveable { mutableStateOf(form.sshUser.isNotBlank()) }
            val showMore = moreOpen || FormError.PORT in form.errors || FormError.STATUS_PORT in form.errors
            val showSsh = sshOpen || FormError.SSH_USER in form.errors || FormError.SSH_PORT in form.errors

            ExpandRow(stringResource(if (showMore) R.string.config_advanced_hide else R.string.config_advanced_show), showMore) { moreOpen = !showMore }
            if (showMore) {
                Field(
                    label = R.string.field_port,
                    value = form.port,
                    error = FormError.PORT in form.errors,
                    errorText = R.string.error_port,
                    number = true,
                ) { v -> viewModel.update { it.copy(port = v) } }

                Text(stringResource(R.string.field_os), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OsType.entries.forEach { os ->
                        FilterChip(
                            selected = form.os == os,
                            onClick = { viewModel.update { it.copy(os = os) } },
                            label = { Text(stringResource(if (os == OsType.WINDOWS) R.string.os_windows else R.string.os_linux)) },
                        )
                    }
                }

                Field(
                    label = R.string.field_status_port,
                    value = form.statusPort,
                    error = FormError.STATUS_PORT in form.errors,
                    errorText = R.string.error_port,
                    supporting = R.string.hint_status_port,
                    number = true,
                ) { v -> viewModel.update { it.copy(statusPort = v) } }

                val groups by viewModel.groups.collectAsStateWithLifecycle()
                var newGroupDialog by remember { mutableStateOf(false) }
                Text(stringResource(R.string.field_group), style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = form.groupId == null,
                        onClick = { viewModel.update { it.copy(groupId = null) } },
                        label = { Text(stringResource(R.string.group_none)) },
                    )
                    groups.forEach { g ->
                        FilterChip(
                            selected = form.groupId == g.id,
                            onClick = { viewModel.update { it.copy(groupId = g.id) } },
                            label = { Text(g.name) },
                        )
                    }
                    AssistChip(onClick = { newGroupDialog = true }, label = { Text(stringResource(R.string.group_new)) })
                }
                if (newGroupDialog) {
                    NameDialog(R.string.group_new, "", onDismiss = { newGroupDialog = false }) { name ->
                        viewModel.createGroup(name)
                        newGroupDialog = false
                    }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.field_show_in_launcher))
                        Text(
                            stringResource(R.string.hint_show_in_launcher),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = form.showInLauncher, onCheckedChange = { v -> viewModel.update { it.copy(showInLauncher = v) } })
                }

            }

            ExpandRow(stringResource(R.string.section_ssh), showSsh) { sshOpen = !showSsh }
            if (showSsh) {
                Field(
                    label = R.string.field_ssh_user,
                    value = form.sshUser,
                    error = FormError.SSH_USER in form.errors,
                    errorText = R.string.error_ssh_user,
                    supporting = R.string.hint_ssh,
                ) { v -> viewModel.update { it.copy(sshUser = v.take(Device.MAX_SSH_USER)) } }
                Field(
                    label = R.string.field_ssh_port,
                    value = form.sshPort,
                    error = FormError.SSH_PORT in form.errors,
                    errorText = R.string.error_port,
                    number = true,
                ) { v -> viewModel.update { it.copy(sshPort = v) } }
                TextButton(onClick = onOpenSshHelp) { Text(stringResource(R.string.help_ssh_link)) }
                if (viewModel.canShutDown) {
                    OutlinedButton(
                        onClick = viewModel::requestShutdown,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.button_shutdown)) }
                }

            }

            if (!viewModel.isNew) {
                OutlinedButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                ) { Text(stringResource(R.string.delete_device)) }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_device)) },
            text = { Text(stringResource(R.string.delete_confirm, form.name)) },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; viewModel.delete() }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }

    ShutdownDialogs(viewModel.shutdownUi, form.name, form.sshUser, viewModel)
}

@Composable
private fun ShutdownDialogs(state: ShutdownUi, deviceName: String, sshUser: String, viewModel: DeviceEditViewModel) {
    when (state) {
        ShutdownUi.Idle -> Unit
        ShutdownUi.Confirm -> AlertDialog(
            onDismissRequest = viewModel::dismissShutdown,
            title = { Text(stringResource(R.string.shutdown_confirm_title, deviceName)) },
            text = { Text(stringResource(R.string.shutdown_confirm_text, sshUser)) },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmShutdown,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text(stringResource(R.string.button_shutdown)) }
            },
            dismissButton = { TextButton(onClick = viewModel::dismissShutdown) { Text(stringResource(R.string.cancel)) } },
        )
        ShutdownUi.Busy -> AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.shutdown_busy)) },
            text = { CircularProgressIndicator() },
            confirmButton = {},
        )
        is ShutdownUi.TrustKey -> AlertDialog(
            onDismissRequest = viewModel::dismissShutdown,
            title = { Text(stringResource(R.string.trust_title)) },
            text = { Text(stringResource(R.string.trust_text, deviceName, state.fingerprint)) },
            confirmButton = { TextButton(onClick = viewModel::trustHostKey) { Text(stringResource(R.string.trust_button)) } },
            dismissButton = { TextButton(onClick = viewModel::dismissShutdown) { Text(stringResource(R.string.cancel)) } },
        )
        is ShutdownUi.KeyChanged -> AlertDialog(
            onDismissRequest = viewModel::dismissShutdown,
            title = { Text(stringResource(R.string.key_changed_title)) },
            text = { Text(stringResource(R.string.key_changed_text, deviceName, state.seen) + "\n\n" + stringResource(R.string.key_changed_old, state.old)) },
            confirmButton = { TextButton(onClick = viewModel::trustHostKey) { Text(stringResource(R.string.key_changed_trust)) } },
            dismissButton = { TextButton(onClick = viewModel::dismissShutdown) { Text(stringResource(R.string.cancel)) } },
        )
        is ShutdownUi.Message -> AlertDialog(
            onDismissRequest = viewModel::dismissShutdown,
            text = { Text(stringResource(state.res, *state.args.toTypedArray())) },
            confirmButton = { TextButton(onClick = viewModel::dismissShutdown) { Text(stringResource(R.string.ok)) } },
        )
    }
}

@Composable
private fun ExpandRow(title: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
    }
}

@Composable
private fun Field(
    label: Int,
    value: String,
    error: Boolean = false,
    errorText: Int? = null,
    supporting: Int? = null,
    number: Boolean = false,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        isError = error,
        supportingText = when {
            error && errorText != null -> ({ Text(stringResource(errorText)) })
            supporting != null -> ({ Text(stringResource(supporting)) })
            else -> null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (number) KeyboardType.Number else KeyboardType.Text),
        modifier = Modifier.fillMaxWidth(),
    )
}
