package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.domain.OsType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceEditScreen(onDone: () -> Unit, viewModel: DeviceEditViewModel = hiltViewModel()) {
    val form = viewModel.form
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.finished) { if (viewModel.finished) onDone() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isNew) R.string.add_device else R.string.edit_device))
                },
                navigationIcon = { TextButton(onClick = onDone) { Text(stringResource(R.string.cancel)) } },
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
            ) { v -> viewModel.update { it.copy(name = v) } }
            Field(
                label = R.string.field_mac,
                value = form.mac,
                error = FormError.MAC in form.errors,
                errorText = R.string.error_mac,
            ) { v -> viewModel.update { it.copy(mac = v) } }
            Field(label = R.string.field_host, value = form.host, supporting = R.string.hint_host) { v ->
                viewModel.update { it.copy(host = v) }
            }
            Field(
                label = R.string.field_broadcast,
                value = form.broadcast,
                error = FormError.BROADCAST in form.errors,
                errorText = R.string.error_broadcast,
                supporting = R.string.hint_broadcast,
            ) { v -> viewModel.update { it.copy(broadcast = v) } }
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
