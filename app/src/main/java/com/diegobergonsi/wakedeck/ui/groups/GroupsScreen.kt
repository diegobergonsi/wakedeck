package com.diegobergonsi.wakedeck.ui.groups

import androidx.compose.material3.IconButton
import com.diegobergonsi.wakedeck.ui.contentSidePadding
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.domain.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(onBack: () -> Unit, viewModel: GroupsViewModel = hiltViewModel()) {
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<GroupRow?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<GroupRow?>(null) }
    var choosing by remember { mutableStateOf<GroupRow?>(null) }
    val allDevices by viewModel.devices.collectAsStateWithLifecycle()
    val groupNames = rows.associate { it.group.id to it.group.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.groups_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { adding = true }) { Text(stringResource(R.string.group_add)) }
        },
    ) { padding ->
        if (rows.isEmpty()) {
            Text(
                stringResource(R.string.groups_empty),
                Modifier.padding(padding).padding(contentSidePadding(24.dp), 24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = contentSidePadding().let { side -> androidx.compose.foundation.layout.PaddingValues(side, 8.dp, side, 96.dp) },
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "hint") {
                    Text(
                        stringResource(R.string.groups_tap_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(rows, key = { it.group.id }) { row ->
                    Card(Modifier.fillMaxWidth().clickable { choosing = row }) {
                        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(row.group.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    pluralStringResource(R.plurals.group_device_count, row.deviceCount, row.deviceCount),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            TextButton(onClick = { editing = row }) { Text(stringResource(R.string.group_rename)) }
                            TextButton(onClick = { deleting = row }) { Text(stringResource(R.string.delete)) }
                        }
                    }
                }
            }
        }
    }

    choosing?.let { row ->
        MembersDialog(
            row = row,
            devices = allDevices,
            groupNames = groupNames,
            onDismiss = { choosing = null },
            onSave = { ids -> viewModel.setMembers(row.group.id, ids); choosing = null },
        )
    }
    if (adding) {
        NameDialog(R.string.group_add, "", onDismiss = { adding = false }) { name -> viewModel.save(0, name); adding = false }
    }
    editing?.let { row ->
        NameDialog(R.string.group_rename, row.group.name, onDismiss = { editing = null }) { name -> viewModel.save(row.group.id, name); editing = null }
    }
    deleting?.let { row ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.group_delete_title, row.group.name)) },
            text = { Text(stringResource(R.string.group_delete_text)) },
            confirmButton = { TextButton(onClick = { viewModel.delete(row.group.id); deleting = null }) { Text(stringResource(R.string.delete)) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Composable
fun NameDialog(titleRes: Int, initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleRes)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.group_name)) },
                singleLine = true,
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

/** Pick which devices belong to a group. A device already in another group is moved here when ticked. */
@Composable
private fun MembersDialog(
    row: GroupRow,
    devices: List<Device>,
    groupNames: Map<Long, String>,
    onDismiss: () -> Unit,
    onSave: (Set<Long>) -> Unit,
) {
    var selected by remember { mutableStateOf(devices.filter { it.groupId == row.group.id }.map { it.id }.toSet()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.group_devices_title, row.group.name)) },
        text = {
            if (devices.isEmpty()) {
                Text(stringResource(R.string.empty_devices))
            } else {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    devices.forEach { d ->
                        val other = d.groupId?.takeIf { it != row.group.id }?.let { groupNames[it] }
                        Row(
                            Modifier.fillMaxWidth().clickable { selected = if (d.id in selected) selected - d.id else selected + d.id },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(checked = d.id in selected, onCheckedChange = { selected = if (it) selected + d.id else selected - d.id })
                            Column {
                                Text(d.name)
                                if (other != null) {
                                    Text(
                                        stringResource(R.string.group_device_in_other, other),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(selected) }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
