package com.diegobergonsi.wakedeck.ui.help

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.diegobergonsi.wakedeck.R
import kotlinx.coroutines.launch

const val HELP_SECTION_WOL = "wol"
const val HELP_SECTION_SSH = "ssh"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit, openSection: String? = null, viewModel: HelpViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedText = stringResource(R.string.help_copied)
    var linux by rememberSaveable { mutableStateOf(true) }
    var open by rememberSaveable { mutableStateOf(openSection ?: HELP_SECTION_WOL) }
    val key = viewModel.publicKeyLine

    val copy: (String) -> Unit = { text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("WakeDeck", text))
        scope.launch { snackbar.showSnackbar(copiedText) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.help_intro), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.help_os_pick), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = linux, onClick = { linux = true }, label = { Text(stringResource(R.string.help_os_linux)) })
                FilterChip(selected = !linux, onClick = { linux = false }, label = { Text(stringResource(R.string.help_os_windows)) })
            }

            Section(R.string.help_s1, open == HELP_SECTION_WOL, { open = if (open == HELP_SECTION_WOL) "" else HELP_SECTION_WOL }) {
                Body(R.string.help_wol_intro)
                Body(R.string.help_wol_bios)
                if (linux) {
                    Body(R.string.help_wol_l1); Command(stringResource(R.string.cmd_linux_list_ifaces), copy)
                    Body(R.string.help_wol_l2); Command(stringResource(R.string.cmd_linux_check_wol), copy)
                    Body(R.string.help_wol_l3); Command(stringResource(R.string.cmd_linux_set_wol), copy)
                    Body(R.string.help_wol_l4); Command(stringResource(R.string.cmd_linux_persist_wol), copy)
                } else {
                    Body(R.string.help_wol_w1)
                    Body(R.string.help_wol_w2)
                    Body(R.string.help_wol_w3)
                }
                Body(R.string.help_wol_app)
                Command(stringResource(if (linux) R.string.cmd_linux_list_ifaces else R.string.cmd_win_ipconfig), copy)
                Body(R.string.help_wol_app2)
            }

            Section(R.string.help_s2, open == HELP_SECTION_SSH, { open = if (open == HELP_SECTION_SSH) "" else HELP_SECTION_SSH }) {
                Body(R.string.help_ssh_intro)
                if (linux) {
                    Body(R.string.help_ssh_l1); Command(stringResource(R.string.cmd_linux_install_ssh), copy)
                    Body(R.string.help_ssh_l1b); Command(stringResource(R.string.cmd_linux_enable_sshd), copy)
                    Body(R.string.help_ssh_l2); Command(stringResource(R.string.cmd_linux_add_key, viewModel.linuxKeyLine), copy)
                    Body(R.string.help_ssh_l3)
                    Command(stringResource(R.string.cmd_linux_sudoers), copy)
                    Command(stringResource(R.string.cmd_linux_sudoers_line), copy)
                } else {
                    Body(R.string.help_ssh_w1); Command(stringResource(R.string.cmd_win_install_ssh), copy)
                    Body(R.string.help_ssh_w2)
                    Command(stringResource(R.string.cmd_win_add_admin_key, key), copy)
                    Command(stringResource(R.string.cmd_win_lock_admin_keys), copy)
                    Body(R.string.help_ssh_w3)
                    Body(R.string.help_ssh_w4)
                }
                Body(R.string.help_ssh_app)
                Command(stringResource(if (linux) R.string.cmd_linux_hostkey else R.string.cmd_win_hostkey), copy)
                Body(R.string.help_ssh_trouble)
            }

            Section(R.string.help_s3, open == "remote", { open = if (open == "remote") "" else "remote" }) {
                Body(R.string.help_remote)
                Body(R.string.help_remote_errors)
            }
            Section(R.string.help_s4, open == "phone", { open = if (open == "phone") "" else "phone" }) {
                val ctx = LocalContext.current
                Body(R.string.help_phone_notif)
                TextButton(onClick = {
                    ctx.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName),
                    )
                }) { Text(stringResource(R.string.help_open_notif_settings)) }
                Body(R.string.help_phone_batt)
                TextButton(onClick = { ctx.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }) {
                    Text(stringResource(R.string.help_open_battery_settings))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Section(title: Int, expanded: Boolean, onToggle: () -> Unit, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, role = Role.Button, onClick = onToggle),
            )
            if (expanded) content()
        }
    }
}

@Composable
private fun Body(res: Int) {
    Text(stringResource(res), style = MaterialTheme.typography.bodyMedium)
}

/** A command in a monospace box with a Copy button. */
@Composable
private fun Command(text: String, onCopy: (String) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
        Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            SelectionContainer(Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text(text, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
            }
            TextButton(onClick = { onCopy(text) }) { Text(stringResource(R.string.help_copy)) }
        }
    }
}
