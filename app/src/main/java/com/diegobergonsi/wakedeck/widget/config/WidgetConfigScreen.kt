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
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.diegobergonsi.wakedeck.widget.WidgetType
import com.diegobergonsi.wakedeck.widget.drawableRes
import com.diegobergonsi.wakedeck.widget.labelRes
import kotlin.math.roundToInt

private val Swatches = listOf(
    0xFF000000, 0xFFFFFFFF, 0xFF1E88E5, 0xFF43A047, 0xFFE53935, 0xFFFB8C00,
    0xFF8E24AA, 0xFF00ACC1, 0xFF6D4C41, 0xFF546E7A, 0xFF212121, 0xFFFDD835,
).map { it.toInt() }

/** Short palette for the status and sent colors, where black and white make no sense. */
private val StateSwatches = listOf(
    0xFF2E9E4F, 0xFFD64545, 0xFF1E88E5, 0xFFFB8C00, 0xFF8E24AA, 0xFFFDD835,
).map { it.toInt() }

private class PreviewOption(val size: DpSize, val label: Int)

private fun previewOptions(type: WidgetType): List<PreviewOption> = when (type) {
    WidgetType.SINGLE -> listOf(
        PreviewOption(DpSize(80.dp, 80.dp), R.string.size_small),
        PreviewOption(DpSize(130.dp, 70.dp), R.string.size_compact),
        PreviewOption(DpSize(220.dp, 70.dp), R.string.size_medium),
        PreviewOption(DpSize(220.dp, 150.dp), R.string.size_large),
    )
    WidgetType.LIST -> listOf(
        PreviewOption(DpSize(280.dp, 170.dp), R.string.size_medium),
        PreviewOption(DpSize(200.dp, 260.dp), R.string.size_large),
    )
    WidgetType.GRID -> listOf(
        PreviewOption(DpSize(190.dp, 90.dp), R.string.size_compact),
        PreviewOption(DpSize(190.dp, 190.dp), R.string.size_medium),
        PreviewOption(DpSize(300.dp, 200.dp), R.string.size_large),
    )
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
    val type = viewModel.type
    val options = remember(type) { previewOptions(type) }
    var advancedOpen by remember { mutableStateOf(false) }
    var previewIndex by remember(type) { mutableIntStateOf(if (type == WidgetType.SINGLE) 2 else 0) }

    LaunchedEffect(devices, viewModel.loaded) {
        if (viewModel.loaded && viewModel.config.deviceIds.isEmpty() && devices.isNotEmpty()) {
            if (viewModel.type == WidgetType.SINGLE) viewModel.selectDevice(devices.first().id) else viewModel.selectAllDevices()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.config_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) { Icon(Icons.Filled.Close, stringResource(R.string.cancel)) }
                },
                actions = {
                    IconButton(onClick = { viewModel.save(onSaved) }, enabled = viewModel.canSave) {
                        Icon(Icons.Filled.Check, stringResource(R.string.save))
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
                        WidgetPreview(widgetId, config, type, options[previewIndex.coerceAtMost(options.lastIndex)].size)
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEachIndexed { index, option ->
                        FilterChip(
                            selected = previewIndex == index,
                            onClick = { previewIndex = index },
                            label = { Text(stringResource(option.label)) },
                        )
                    }
                }
            }

            Section(if (type == WidgetType.SINGLE) R.string.config_device else R.string.config_devices) {
                devices.forEach { device ->
                    val selected = device.id in config.deviceIds
                    val toggle = { if (type == WidgetType.SINGLE) viewModel.selectDevice(device.id) else viewModel.toggleDevice(device.id) }
                    Row(Modifier.fillMaxWidth().clickable { toggle() }, verticalAlignment = Alignment.CenterVertically) {
                        if (type == WidgetType.SINGLE) {
                            RadioButton(selected = selected, onClick = { toggle() })
                        } else {
                            Checkbox(checked = selected, onCheckedChange = { toggle() })
                        }
                        Text(device.name)
                    }
                }
            }

            Section(R.string.config_theme) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WidgetThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = config.themeMode == mode,
                            // Custom colors win over the theme, so choosing a theme clears them; otherwise the preview would not change.
                            onClick = { viewModel.update { it.copy(themeMode = mode, backgroundColor = null, accentColor = null) } },
                            label = {
                                Text(
                                    stringResource(
                                        when (mode) {
                                            WidgetThemeMode.BRAND -> R.string.app_name
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

            if (type != WidgetType.LIST) Section(R.string.config_icon) {
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
                            Icon(painterResource(icon.drawableRes()), contentDescription = stringResource(icon.labelRes()), Modifier.size(24.dp))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = config.label,
                onValueChange = { v -> viewModel.update { it.copy(label = v) } },
                label = { Text(stringResource(R.string.config_label)) },
                supportingText = {
                    Text(stringResource(if (type == WidgetType.SINGLE) R.string.config_label_hint else R.string.config_label_hint_multi))
                },
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

            if (type != WidgetType.SINGLE) {
                Section(R.string.config_density) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DensityOption.entries.forEach { option ->
                            FilterChip(
                                selected = config.density == option,
                                onClick = { viewModel.update { it.copy(density = option) } },
                                label = {
                                    Text(stringResource(if (option == DensityOption.COMPACT) R.string.density_compact else R.string.density_comfortable))
                                },
                            )
                        }
                    }
                }
            }
            if (type == WidgetType.LIST) {
                Section(R.string.config_row_style) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListRowStyle.entries.forEach { option ->
                            FilterChip(
                                selected = config.rowStyle == option,
                                onClick = { viewModel.update { it.copy(rowStyle = option) } },
                                label = {
                                    Text(stringResource(if (option == ListRowStyle.DETAILED) R.string.row_detailed else R.string.row_minimal))
                                },
                            )
                        }
                    }
                }
            }
            if (type == WidgetType.GRID) {
                Section(R.string.config_columns) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (0..WidgetConfig.MAX_GRID_COLUMNS).forEach { n ->
                            FilterChip(
                                selected = config.gridColumns == n,
                                onClick = { viewModel.update { it.copy(gridColumns = n) } },
                                label = { Text(if (n == 0) stringResource(R.string.columns_auto) else n.toString()) },
                            )
                        }
                    }
                }
            }

            Section(R.string.config_text_size) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            TextButton(onClick = { advancedOpen = !advancedOpen }) {
                Text(stringResource(if (advancedOpen) R.string.config_advanced_hide else R.string.config_advanced_show))
            }
            if (advancedOpen) {
                Section(R.string.config_color_online) {
                    ColorPicker(config.onlineColor, StateSwatches, R.string.color_default) { c -> viewModel.update { it.copy(onlineColor = c) } }
                }
                Section(R.string.config_color_offline) {
                    ColorPicker(config.offlineColor, StateSwatches, R.string.color_default) { c -> viewModel.update { it.copy(offlineColor = c) } }
                }
                Section(R.string.config_color_sent) {
                    ColorPicker(config.sentColor, StateSwatches, R.string.color_default) { c -> viewModel.update { it.copy(sentColor = c) } }
                }
                Section(R.string.config_border) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (0..WidgetConfig.MAX_BORDER_WIDTH_DP).forEach { w ->
                            FilterChip(
                                selected = config.borderWidthDp == w,
                                onClick = { viewModel.update { it.copy(borderWidthDp = w) } },
                                label = { Text(if (w == 0) stringResource(R.string.border_none) else "$w dp") },
                            )
                        }
                    }
                }
                if (config.borderWidthDp > 0) {
                    Section(R.string.config_border_color) {
                        ColorPicker(config.borderColor) { c -> viewModel.update { it.copy(borderColor = c) } }
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
private fun ColorPicker(
    selected: Int?,
    swatches: List<Int> = Swatches,
    defaultLabel: Int = R.string.color_theme_default,
    onPick: (Int?) -> Unit,
) {
    val swatchSize = if (swatches.size <= 6) 32.dp else 36.dp
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = selected == null, onClick = { onPick(null) }, label = { Text(stringResource(defaultLabel)) })
        swatches.forEach { argb ->
            val isSelected = selected == argb
            Box(
                Modifier
                    .size(swatchSize)
                    .background(Color(argb), CircleShape)
                    .border(
                        BorderStroke(if (isSelected) 3.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                        CircleShape,
                    )
                    .clickable { onPick(argb) },
            )
        }
    }
    HexField(selected, onPick)
}

/** Free color input. Only a complete `#RRGGBB` value changes the color, so half-typed text is harmless. */
@Composable
private fun HexField(selected: Int?, onPick: (Int?) -> Unit) {
    var text by remember(selected) { mutableStateOf(selected?.let { ColorHex.format(it) } ?: "") }
    OutlinedTextField(
        value = text,
        onValueChange = { value ->
            text = value
            ColorHex.parse(value)?.let { onPick(it) }
        },
        label = { Text(stringResource(R.string.config_hex)) },
        isError = text.isNotBlank() && ColorHex.parse(text) == null,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(0.7f),
    )
}
