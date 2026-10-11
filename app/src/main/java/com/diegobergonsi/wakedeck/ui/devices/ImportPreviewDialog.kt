package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.transfer.ImportPlan
import com.diegobergonsi.wakedeck.data.transfer.TransferDevice
import com.diegobergonsi.wakedeck.domain.AddressScope

private const val MAX_ROWS = 50

/** Shows what an import file would add, so a changed file can be spotted before it is imported. */
@Composable
fun ImportPreviewDialog(plan: ImportPlan, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val flagged = plan.toAdd.any { it.pointsOutside() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.import_preview_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.import_preview_summary, plan.toAdd.size, plan.skippedExisting, plan.invalid),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (flagged) {
                    Text(
                        stringResource(R.string.import_preview_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                LazyColumn(Modifier.heightIn(max = 320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(plan.toAdd.take(MAX_ROWS)) { d -> Row(d) }
                    if (plan.toAdd.size > MAX_ROWS) {
                        item { Text(stringResource(R.string.import_preview_more, plan.toAdd.size - MAX_ROWS), style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onConfirm, enabled = plan.toAdd.isNotEmpty()) { Text(stringResource(R.string.import_preview_confirm)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

private fun TransferDevice.pointsOutside() = !AddressScope.isLocal(host) || !AddressScope.isLocal(broadcastAddress)

@Composable
private fun Row(d: TransferDevice) {
    Column {
        Text(d.name, style = MaterialTheme.typography.titleSmall)
        val host = d.host.ifEmpty { stringResource(R.string.status_no_host) }
        Text("$host · ${d.broadcastAddress}:${d.port}", style = MaterialTheme.typography.bodySmall)
        if (d.sshUser.isNotEmpty()) Text(stringResource(R.string.import_preview_ssh, d.sshUser, d.sshPort), style = MaterialTheme.typography.bodySmall)
        if (d.pointsOutside()) {
            Text(stringResource(R.string.import_preview_outside), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
    }
}
