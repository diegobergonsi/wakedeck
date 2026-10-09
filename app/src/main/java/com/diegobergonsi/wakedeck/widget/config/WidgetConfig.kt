package com.diegobergonsi.wakedeck.widget.config

enum class WidgetThemeMode { MATERIAL_YOU, LIGHT, DARK }

enum class TextSizeOption(val scale: Float) { SMALL(0.85f), MEDIUM(1f), LARGE(1.2f) }

enum class DensityOption(val rowPaddingDp: Int) { COMPACT(3), COMFORTABLE(8) }

enum class ListRowStyle { DETAILED, MINIMAL }

enum class WidgetIcon { POWER, DESKTOP, LAPTOP, SERVER, TV, GAMEPAD, HOME, BOLT, CLOUD, ROUTER, STAR, CHIP }

/**
 * Per-widget settings. Colors are ARGB ints; null means "use the theme". Everything has a default so a widget
 * that has no stored value for a field still renders.
 */
data class WidgetConfig(
    val deviceIds: List<Long> = emptyList(),
    val themeMode: WidgetThemeMode = WidgetThemeMode.MATERIAL_YOU,
    val backgroundColor: Int? = null,
    val accentColor: Int? = null,
    /** 0f = fully transparent, 1f = opaque. Applies to the background only. */
    val backgroundAlpha: Float = 1f,
    val cornerRadiusDp: Int = DEFAULT_CORNER_RADIUS_DP,
    val icon: WidgetIcon = WidgetIcon.POWER,
    /** Blank means "use the device name". */
    val label: String = "",
    val showStatus: Boolean = true,
    /** Small refresh icon that re-checks the device status right away. */
    val showRefresh: Boolean = true,
    val textSize: TextSizeOption = TextSizeOption.MEDIUM,
    val density: DensityOption = DensityOption.COMFORTABLE,
    val rowStyle: ListRowStyle = ListRowStyle.DETAILED,
    /** Grid columns, 0 = automatic from the widget width. */
    val gridColumns: Int = 0,
    /** Colors for the status dot and button states. Null = built-in default. */
    val onlineColor: Int? = null,
    val offlineColor: Int? = null,
    val sentColor: Int? = null,
    /** 0 = no border. */
    val borderWidthDp: Int = 0,
    val borderColor: Int? = null,
) {
    companion object {
        const val DEFAULT_CORNER_RADIUS_DP = 24
        const val MAX_CORNER_RADIUS_DP = 40
        const val MAX_GRID_COLUMNS = 4
        const val MAX_BORDER_WIDTH_DP = 4
    }
}
