package com.diegobergonsi.wakedeck.ui.ssh

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.diegobergonsi.wakedeck.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SshKeyScreen(onBack: () -> Unit, viewModel: SshKeyViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val copiedText = stringResource(R.string.ssh_key_copied)
    var confirmRegenerate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ssh_key_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(stringResource(R.string.back)) } },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.ssh_key_intro), style = MaterialTheme.typography.bodyMedium)
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                SelectionContainer {
                    Text(
                        viewModel.publicKeyLine,
                        Modifier.fillMaxWidth().padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
            Text(stringResource(R.string.ssh_key_fingerprint, viewModel.fingerprint), style = MaterialTheme.typography.labelMedium)
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("WakeDeck SSH public key", viewModel.publicKeyLine))
                    Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.ssh_key_copy)) }

            Text(stringResource(R.string.ssh_key_linux_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.ssh_key_linux_text), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.ssh_key_windows_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.ssh_key_windows_text), style = MaterialTheme.typography.bodyMedium)

            OutlinedButton(onClick = { confirmRegenerate = true }, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Text(stringResource(R.string.ssh_key_regenerate))
            }
        }
    }

    if (confirmRegenerate) {
        AlertDialog(
            onDismissRequest = { confirmRegenerate = false },
            title = { Text(stringResource(R.string.ssh_key_regenerate)) },
            text = { Text(stringResource(R.string.ssh_key_regenerate_warning)) },
            confirmButton = {
                TextButton(onClick = { confirmRegenerate = false; viewModel.regenerate() }) {
                    Text(stringResource(R.string.ssh_key_regenerate))
                }
            },
            dismissButton = { TextButton(onClick = { confirmRegenerate = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}
