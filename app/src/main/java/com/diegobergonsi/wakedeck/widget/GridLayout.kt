package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln

object GridLayout {
    private const val AUTO_CELL_DP = 72f
    private const val MIN_CELL_DP = 56f
    private const val WANTED_ASPECT = 1.3f
    private const val MIN_TILE_HEIGHT_DP = 66f
    private const val MAX_TILE_HEIGHT_DP = 120f

    /** Requested columns (0 = auto), limited so each column keeps at least [MIN_CELL_DP]. */
    fun columnsFor(widthDp: Float, requested: Int): Int {
        val fits = (widthDp / MIN_CELL_DP).toInt().coerceAtLeast(1)
        val wanted = if (requested > 0) requested else (widthDp / AUTO_CELL_DP).toInt()
        return wanted.coerceIn(1, WidgetConfig.MAX_GRID_COLUMNS).coerceAtMost(fits)
    }

    /**
     * Columns for the auto setting: the count that makes tiles closest to [WANTED_ASPECT] (a bit wider than tall)
     * once they share the widget height. A tall slot with few devices gets one stacked column, a wide short one
     * gets a row.
     */
    fun autoColumns(widthDp: Float, heightDp: Float, deviceCount: Int): Int {
        if (deviceCount <= 1) return 1
        val maxColumns = columnsFor(widthDp, WidgetConfig.MAX_GRID_COLUMNS).coerceAtMost(deviceCount)
        return (1..maxColumns).minBy { columns ->
            val rows = ceil(deviceCount / columns.toFloat())
            val tileWidth = widthDp / columns
            val tileHeight = (heightDp / rows).coerceIn(MIN_TILE_HEIGHT_DP, MAX_TILE_HEIGHT_DP)
            abs(ln(tileWidth / tileHeight / WANTED_ASPECT))
        }
    }

    /** Height each tile row may use so the rows share the available height, within sensible limits. */
    fun tileHeightDp(availableHeightDp: Float, rows: Int): Float =
        (availableHeightDp / rows.coerceAtLeast(1)).coerceIn(MIN_TILE_HEIGHT_DP, MAX_TILE_HEIGHT_DP)
}
