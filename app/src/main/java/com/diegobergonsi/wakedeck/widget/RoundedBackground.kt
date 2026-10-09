package com.diegobergonsi.wakedeck.widget

import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.ContentScale
import androidx.glance.unit.ColorProvider
import com.diegobergonsi.wakedeck.R

private val roundedRes = mapOf(
    4 to R.drawable.bg_round_4, 8 to R.drawable.bg_round_8, 12 to R.drawable.bg_round_12,
    16 to R.drawable.bg_round_16, 20 to R.drawable.bg_round_20, 24 to R.drawable.bg_round_24,
    28 to R.drawable.bg_round_28, 32 to R.drawable.bg_round_32, 36 to R.drawable.bg_round_36,
    40 to R.drawable.bg_round_40,
)

/**
 * Rounded, tinted background drawn as a drawable instead of clipping the view. Clipping is lost when the launcher
 * snapshots the widget while you drag it, which showed square corners. Radius snaps to the nearest 4 dp.
 */
fun GlanceModifier.roundedBackground(radiusDp: Int, color: ColorProvider): GlanceModifier {
    val snapped = ((radiusDp + 2) / 4) * 4
    val res = roundedRes[snapped.coerceAtMost(40)] ?: return background(color)
    return background(ImageProvider(res), ContentScale.FillBounds, ColorFilter.tint(color))
}

fun GlanceModifier.dotBackground(color: ColorProvider): GlanceModifier =
    background(ImageProvider(R.drawable.bg_dot), ContentScale.FillBounds, ColorFilter.tint(color))
