package com.diegobergonsi.wakedeck.widget.config

/** Converts [WidgetConfig] to and from a flat string map. Unknown or bad values fall back to defaults. */
object WidgetConfigCodec {
    private const val DEVICE_IDS = "deviceIds"
    private const val THEME = "theme"
    private const val BACKGROUND = "background"
    private const val ACCENT = "accent"
    private const val ALPHA = "alpha"
    private const val RADIUS = "radius"
    private const val ICON = "icon"
    private const val LABEL = "label"
    private const val SHOW_STATUS = "showStatus"
    private const val SHOW_REFRESH = "showRefresh"
    private const val TEXT_SIZE = "textSize"
    private const val DENSITY = "density"
    private const val ROW_STYLE = "rowStyle"
    private const val GRID_COLUMNS = "gridColumns"
    private const val ONLINE = "onlineColor"
    private const val OFFLINE = "offlineColor"
    private const val SENT = "sentColor"
    private const val BORDER_WIDTH = "borderWidth"
    private const val BORDER_COLOR = "borderColor"

    val fields = listOf(DEVICE_IDS, THEME, BACKGROUND, ACCENT, ALPHA, RADIUS, ICON, LABEL, SHOW_STATUS, SHOW_REFRESH, TEXT_SIZE, DENSITY, ROW_STYLE, GRID_COLUMNS,
        ONLINE, OFFLINE, SENT, BORDER_WIDTH, BORDER_COLOR,
    )

    fun encode(config: WidgetConfig): Map<String, String> = buildMap {
        put(DEVICE_IDS, config.deviceIds.joinToString(","))
        put(THEME, config.themeMode.name)
        config.backgroundColor?.let { put(BACKGROUND, it.toString()) }
        config.accentColor?.let { put(ACCENT, it.toString()) }
        put(ALPHA, config.backgroundAlpha.toString())
        put(RADIUS, config.cornerRadiusDp.toString())
        put(ICON, config.icon.name)
        put(LABEL, config.label)
        put(SHOW_STATUS, config.showStatus.toString())
        put(SHOW_REFRESH, config.showRefresh.toString())
        put(TEXT_SIZE, config.textSize.name)
        put(DENSITY, config.density.name)
        put(ROW_STYLE, config.rowStyle.name)
        put(GRID_COLUMNS, config.gridColumns.toString())
        config.onlineColor?.let { put(ONLINE, it.toString()) }
        config.offlineColor?.let { put(OFFLINE, it.toString()) }
        config.sentColor?.let { put(SENT, it.toString()) }
        put(BORDER_WIDTH, config.borderWidthDp.toString())
        config.borderColor?.let { put(BORDER_COLOR, it.toString()) }
    }

    fun decode(map: Map<String, String>): WidgetConfig {
        val d = WidgetConfig()
        return WidgetConfig(
            deviceIds = map[DEVICE_IDS].orEmpty().split(',').mapNotNull { it.trim().toLongOrNull() },
            themeMode = enumOr(map[THEME], d.themeMode),
            backgroundColor = map[BACKGROUND]?.toIntOrNull(),
            accentColor = map[ACCENT]?.toIntOrNull(),
            backgroundAlpha = (map[ALPHA]?.toFloatOrNull() ?: d.backgroundAlpha).coerceIn(0f, 1f),
            cornerRadiusDp = (map[RADIUS]?.toIntOrNull() ?: d.cornerRadiusDp)
                .coerceIn(0, WidgetConfig.MAX_CORNER_RADIUS_DP),
            icon = enumOr(map[ICON], d.icon),
            label = map[LABEL].orEmpty(),
            showStatus = map[SHOW_STATUS]?.toBooleanStrictOrNull() ?: d.showStatus,
            showRefresh = map[SHOW_REFRESH]?.toBooleanStrictOrNull() ?: d.showRefresh,
            textSize = enumOr(map[TEXT_SIZE], d.textSize),
            density = enumOr(map[DENSITY], d.density),
            rowStyle = enumOr(map[ROW_STYLE], d.rowStyle),
            gridColumns = (map[GRID_COLUMNS]?.toIntOrNull() ?: d.gridColumns).coerceIn(0, WidgetConfig.MAX_GRID_COLUMNS),
            onlineColor = map[ONLINE]?.toIntOrNull(),
            offlineColor = map[OFFLINE]?.toIntOrNull(),
            sentColor = map[SENT]?.toIntOrNull(),
            borderWidthDp = (map[BORDER_WIDTH]?.toIntOrNull() ?: d.borderWidthDp).coerceIn(0, WidgetConfig.MAX_BORDER_WIDTH_DP),
            borderColor = map[BORDER_COLOR]?.toIntOrNull(),
        )
    }

    private inline fun <reified E : Enum<E>> enumOr(name: String?, default: E): E =
        enumValues<E>().firstOrNull { it.name == name } ?: default
}
