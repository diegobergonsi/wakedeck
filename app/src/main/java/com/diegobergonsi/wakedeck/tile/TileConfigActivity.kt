package com.diegobergonsi.wakedeck.tile

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.ui.theme.WakeDeckTheme
import dagger.hilt.android.AndroidEntryPoint

/** Opens from the Quick Settings tile (long-press, or tap an empty tile) to choose the device it wakes. */
@AndroidEntryPoint
class TileConfigActivity : ComponentActivity() {
    private val viewModel: TileConfigViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val slot = slotFrom(intent)
        if (slot == null) {
            finish()
            return
        }
        viewModel.start(slot)
        enableEdgeToEdge()
        setContent {
            WakeDeckTheme {
                val devices by viewModel.devices.collectAsStateWithLifecycle()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.tile_config_title, slot)) },
                            navigationIcon = { TextButton(onClick = { finish() }) { Text(stringResource(R.string.cancel)) } },
                            actions = { TextButton(onClick = { viewModel.save { finish() } }) { Text(stringResource(R.string.save)) } },
                        )
                    },
                ) { padding ->
                    Column(
                        Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (devices.isEmpty()) {
                            Text(stringResource(R.string.config_no_devices), style = MaterialTheme.typography.bodyLarge)
                        }
                        devices.forEach { device ->
                            Row(
                                Modifier.fillMaxWidth().clickable { viewModel.select(device.id) },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(selected = viewModel.selected == device.id, onClick = { viewModel.select(device.id) })
                                Text(device.name)
                            }
                        }
                        if (devices.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth().clickable { viewModel.select(null) },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(selected = viewModel.selected == null, onClick = { viewModel.select(null) })
                                Text(stringResource(R.string.tile_no_device))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun slotFrom(intent: Intent?): Int? {
        val fromComponent = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(Intent.EXTRA_COMPONENT_NAME, ComponentName::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Intent.EXTRA_COMPONENT_NAME)
        }
        return TileUi.slotFor(fromComponent?.className)
            ?: intent?.getIntExtra(EXTRA_SLOT, 0)?.takeIf { it in 1..TileUi.SLOTS }
    }

    companion object {
        const val EXTRA_SLOT = "slot"
    }
}
