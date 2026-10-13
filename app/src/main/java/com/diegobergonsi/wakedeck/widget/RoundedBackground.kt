package com.diegobergonsi.wakedeck.widget

import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.ContentScale
import androidx.glance.unit.ColorProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig

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
fun GlanceModifier.roundedBackground(radiusDp: Int, color: ColorProvider, translucent: Boolean = false): GlanceModifier {
    // The launcher ignores the alpha of a tint, so a see-through color is drawn as a plain background with a clipped corner
    // radius (Android 12+). shortcut: corners look square while the widget is dragged, and stay square below Android 12.
    if (translucent) return background(color).cornerRadius(radiusDp.dp)
    val snapped = ((radiusDp + 2) / 4) * 4
    val res = roundedRes[snapped.coerceAtMost(40)] ?: return background(color)
    return background(ImageProvider(res), ContentScale.FillBounds, ColorFilter.tint(color))
}

fun GlanceModifier.dotBackground(color: ColorProvider): GlanceModifier =
    background(ImageProvider(R.drawable.bg_dot), ContentScale.FillBounds, ColorFilter.tint(color))

private val ringRes = mapOf(
    0 to mapOf(1 to R.drawable.bg_ring_0_1, 2 to R.drawable.bg_ring_0_2, 3 to R.drawable.bg_ring_0_3, 4 to R.drawable.bg_ring_0_4),
    4 to mapOf(1 to R.drawable.bg_ring_4_1, 2 to R.drawable.bg_ring_4_2, 3 to R.drawable.bg_ring_4_3, 4 to R.drawable.bg_ring_4_4),
    8 to mapOf(1 to R.drawable.bg_ring_8_1, 2 to R.drawable.bg_ring_8_2, 3 to R.drawable.bg_ring_8_3, 4 to R.drawable.bg_ring_8_4),
    12 to mapOf(1 to R.drawable.bg_ring_12_1, 2 to R.drawable.bg_ring_12_2, 3 to R.drawable.bg_ring_12_3, 4 to R.drawable.bg_ring_12_4),
    16 to mapOf(1 to R.drawable.bg_ring_16_1, 2 to R.drawable.bg_ring_16_2, 3 to R.drawable.bg_ring_16_3, 4 to R.drawable.bg_ring_16_4),
    20 to mapOf(1 to R.drawable.bg_ring_20_1, 2 to R.drawable.bg_ring_20_2, 3 to R.drawable.bg_ring_20_3, 4 to R.drawable.bg_ring_20_4),
    24 to mapOf(1 to R.drawable.bg_ring_24_1, 2 to R.drawable.bg_ring_24_2, 3 to R.drawable.bg_ring_24_3, 4 to R.drawable.bg_ring_24_4),
    28 to mapOf(1 to R.drawable.bg_ring_28_1, 2 to R.drawable.bg_ring_28_2, 3 to R.drawable.bg_ring_28_3, 4 to R.drawable.bg_ring_28_4),
    32 to mapOf(1 to R.drawable.bg_ring_32_1, 2 to R.drawable.bg_ring_32_2, 3 to R.drawable.bg_ring_32_3, 4 to R.drawable.bg_ring_32_4),
    36 to mapOf(1 to R.drawable.bg_ring_36_1, 2 to R.drawable.bg_ring_36_2, 3 to R.drawable.bg_ring_36_3, 4 to R.drawable.bg_ring_36_4),
    40 to mapOf(1 to R.drawable.bg_ring_40_1, 2 to R.drawable.bg_ring_40_2, 3 to R.drawable.bg_ring_40_3, 4 to R.drawable.bg_ring_40_4),
)

/** Border drawn as a transparent ring on top of the background, so a see-through background keeps a solid border. */
@Composable
fun BorderOverlay(config: WidgetConfig, palette: WidgetPalette) {
    if (config.borderWidthDp <= 0) return
    val snapped = (((config.cornerRadiusDp + 2) / 4) * 4).coerceIn(0, 40)
    val res = ringRes[snapped]?.get(config.borderWidthDp.coerceIn(1, 4)) ?: return
    Box(GlanceModifier.fillMaxSize().background(ImageProvider(res), ContentScale.FillBounds, ColorFilter.tint(palette.border))) {}
}
