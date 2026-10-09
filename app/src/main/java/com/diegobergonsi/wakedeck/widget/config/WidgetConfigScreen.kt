package com.diegobergonsi.wakedeck.widget.config

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.widget.drawableRes
import kotlin.math.roundToInt

private val Swatches = listOf(
    0xFF000000, 0xFFFFFFFF, 0xFF1E88E5, 0xFF43A047, 0xFFE53935, 0xFFFB8C00,
    0xFF8E24AA, 0xFF00ACC1, 0xFF6D4C41, 0xFF546E7A, 0xFF212121, 0xFFFDD835,
).map { it.toInt() }

private enum class PreviewSize(val size: DpSize, val label: Int) {
    SMALL(DpSize(80.dp, 80.dp), R.string.size_small),
    COMPACT(DpSize(130.dp, 70.dp), R.string.size_compact),
    WIDE(DpSize(220.dp, 70.dp), R.string.size_medium),
    LARGE(DpSize(220.dp, 150.dp), R.string.size_large),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WidgetConfigScreen(
    viewModel: WidgetConfigViewModel,
    widgetId: Int,
    onCancel: () -> Unit,
    onSaved: () -> Unit,
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val config = viewModel.config
    var previewSize by remember { mutableStateOf(PreviewSize.WIDE) }

    LaunchedEffect(devices, viewModel.loaded) {
        if (viewModel.loaded && viewModel.config.deviceIds.isEmpty() && devices.isNotEmpty()) {
            viewModel.selectDevice(devices.first().id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.config_title)) },
                navigationIcon = { TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) } },
                actions = {
                    TextButton(onClick = { viewModel.save(onSaved) }, enabled = viewModel.canSave) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (devices.isEmpty()) {
                Text(stringResource(R.string.config_no_devices), style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            Section(R.string.config_preview) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        WidgetPreview(widgetId, config, previewSize.size)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreviewSize.entries.forEach { s ->
                        FilterChip(
                            selected = previewSize == s,
                            onClick = { previewSize = s },
                            label = { Text(stringResource(s.label)) },
                        )
                    }
                }
            }

            Section(R.string.config_device) {
                devices.forEach { device ->
                    Row(
                        Modifier.fillMaxWidth().clickable { viewModel.selectDevice(device.id) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = config.deviceIds.firstOrNull() == device.id, onClick = { viewModel.selectDevice(device.id) })
                        Text(device.name)
                    }
                }
            }

            Section(R.string.config_theme) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WidgetThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = config.themeMode == mode,
                            onClick = { viewModel.update { it.copy(themeMode = mode) } },
                            label = {
                                Text(
                                    stringResource(
                                        when (mode) {
                                            WidgetThemeMode.MATERIAL_YOU -> R.string.theme_material_you
                                            WidgetThemeMode.LIGHT -> R.string.theme_light
                                            WidgetThemeMode.DARK -> R.string.theme_dark
                                        },
                                    ),
                                )
                            },
                        )
                    }
                }
            }

            Section(R.string.config_background) {
                ColorPicker(config.backgroundColor) { c -> viewModel.update { it.copy(backgroundColor = c) } }
            }
            Section(R.string.config_accent) {
                ColorPicker(config.accentColor) { c -> viewModel.update { it.copy(accentColor = c) } }
            }

            Column {
                Text(
                    stringResource(R.string.config_transparency, ((1f - config.backgroundAlpha) * 100).roundToInt()),
                    style = MaterialTheme.typography.labelLarge,
                )
                Slider(
                    value = 1f - config.backgroundAlpha,
                    onValueChange = { v -> viewModel.update { it.copy(backgroundAlpha = 1f - v) } },
                )
            }
            Column {
                Text(
                    stringResource(R.string.config_corner, config.cornerRadiusDp),
                    style = MaterialTheme.typography.labelLarge,
                )
                Slider(
                    value = config.cornerRadiusDp.toFloat(),
                    onValueChange = { v -> viewModel.update { it.copy(cornerRadiusDp = v.roundToInt()) } },
                    valueRange = 0f..WidgetConfig.MAX_CORNER_RADIUS_DP.toFloat(),
                )
            }

            Section(R.string.config_icon) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WidgetIcon.entries.forEach { icon ->
                        val selected = config.icon == icon
                        Box(
                            Modifier
                                .size(48.dp)
                                .border(
                                    BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                    RoundedCornerShape(12.dp),
                                )
                                .clickable { viewModel.update { it.copy(icon = icon) } },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(painterResource(icon.drawableRes()), contentDescription = null, Modifier.size(24.dp))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = config.label,
                onValueChange = { v -> viewModel.update { it.copy(label = v) } },
                label = { Text(stringResource(R.string.config_label)) },
                supportingText = { Text(stringResource(R.string.config_label_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.config_show_status), Modifier.weight(1f))
                Switch(checked = config.showStatus, onCheckedChange = { v -> viewModel.update { it.copy(showStatus = v) } })
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.config_show_refresh), Modifier.weight(1f))
                Switch(checked = config.showRefresh, onCheckedChange = { v -> viewModel.update { it.copy(showRefresh = v) } })
            }

            Section(R.string.config_text_size) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextSizeOption.entries.forEach { option ->
                        FilterChip(
                            selected = config.textSize == option,
                            onClick = { viewModel.update { it.copy(textSize = option) } },
                            label = {
                                Text(
                                    stringResource(
                                        when (option) {
                                            TextSizeOption.SMALL -> R.string.size_small
                                            TextSizeOption.MEDIUM -> R.string.size_medium
                                            TextSizeOption.LARGE -> R.string.size_large
                                        },
                                    ),
                                )
                            },
                        )
                    }
                }
            }
            Box(Modifier.size(24.dp))
        }
    }
}

@Composable
private fun Section(title: Int, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.labelLarge)
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorPicker(selected: Int?, onPick: (Int?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = selected == null, onClick = { onPick(null) }, label = { Text(stringResource(R.string.color_theme_default)) })
        Swatches.forEach { argb ->
            val isSelected = selected == argb
            Box(
                Modifier
                    .size(36.dp)
                    .background(Color(argb), CircleShape)
                    .border(
                        BorderStroke(if (isSelected) 3.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                        CircleShape,
                    )
                    .clickable { onPick(argb) },
            )
        }
    }
}
