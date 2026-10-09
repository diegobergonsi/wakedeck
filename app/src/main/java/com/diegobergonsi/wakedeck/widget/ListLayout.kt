package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.DensityOption

object ListLayout {
    /** Rows share the available height; the density option sets how short or tall a row may get. */
    fun rowHeightDp(availableHeightDp: Float, rows: Int, density: DensityOption): Float {
        val (min, max) = when (density) {
            DensityOption.COMPACT -> 36f to 56f
            DensityOption.COMFORTABLE -> 48f to 76f
        }
        return (availableHeightDp / rows.coerceAtLeast(1)).coerceIn(min, max)
    }
}
