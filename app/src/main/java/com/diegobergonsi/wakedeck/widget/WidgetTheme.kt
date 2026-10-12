package com.diegobergonsi.wakedeck.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import com.diegobergonsi.wakedeck.ui.theme.BrandDarkColors
import com.diegobergonsi.wakedeck.ui.theme.BrandLightColors
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import com.diegobergonsi.wakedeck.widget.config.WidgetThemeMode

class WidgetPalette(
    val background: ColorProvider,
    val text: ColorProvider,
    val subtext: ColorProvider,
    val accent: ColorProvider,
    val onAccent: ColorProvider,
    val online: ColorProvider,
    val offline: ColorProvider,
    val neutral: ColorProvider,
    val sent: ColorProvider,
    val border: ColorProvider,
    /** Faint panel tone for a card nested inside the widget background. */
    val card: ColorProvider,
    /** Translucent accent for tonal (low-emphasis) buttons; text on it uses [accent]. */
    val accentSoft: ColorProvider,
    /** Readable text on [accentSoft]. */
    val onAccentSoft: ColorProvider,
    /** Readable text on the solid [sent] and [offline] fills. */
    val onSent: ColorProvider,
    val onOffline: ColorProvider,
)

private val OnlineGreen = Color(0xFF2E9E4F)
private val OfflineRed = Color(0xFFD64545)

@Composable
fun WidgetTheme(mode: WidgetThemeMode, content: @Composable () -> Unit) {
    when (mode) {
        WidgetThemeMode.MATERIAL_YOU -> GlanceTheme(content = content)
        WidgetThemeMode.LIGHT -> GlanceTheme(colors = ColorProviders(BrandLightColors), content = content)
        WidgetThemeMode.DARK, WidgetThemeMode.BRAND -> GlanceTheme(colors = ColorProviders(BrandDarkColors), content = content)
    }
}

/** Must be called inside [WidgetTheme]. Custom colors win over the theme; transparency applies to the background. */
@Composable
fun widgetPalette(config: WidgetConfig): WidgetPalette {
    val theme = GlanceTheme.colors
    val context = LocalContext.current

    val customBg = config.backgroundColor?.let { Color(it) }
    val bgColor = (customBg ?: theme.widgetBackground.getColor(context)).copy(alpha = config.backgroundAlpha)
    val text = if (customBg != null) ColorProvider(bestOn(customBg)) else theme.onSurface
    val subtext = if (customBg != null) ColorProvider(softText(customBg)) else theme.onSurfaceVariant

    val customAccent = config.accentColor?.let { Color(it) }
    // Alpha is lost when the launcher applies a tint to a drawable, so these are blended into opaque colors here.
    val textColor = if (customBg != null) (if (customBg.luminance() > 0.5f) Color.Black else Color.White) else theme.onSurface.getColor(context)
    val cardColor = lerp(customBg ?: theme.widgetBackground.getColor(context), textColor, 0.07f)
    val accentColor = customAccent ?: theme.primary.getColor(context)
    val softColor = lerp(cardColor, accentColor, 0.25f)
    val sentColor = config.sentColor?.let { Color(it) } ?: config.onlineColor?.let { Color(it) } ?: OnlineGreen
    val offlineColor = config.offlineColor?.let { Color(it) } ?: OfflineRed
    return WidgetPalette(
        background = ColorProvider(bgColor),
        text = text,
        subtext = subtext,
        accent = customAccent?.let { ColorProvider(it) } ?: theme.primary,
        onAccent = customAccent?.let { ColorProvider(bestOn(it)) } ?: theme.onPrimary,
        online = ColorProvider(config.onlineColor?.let { Color(it) } ?: OnlineGreen),
        offline = ColorProvider(offlineColor),
        neutral = theme.outline,
        sent = ColorProvider(sentColor),
        border = config.borderColor?.let { ColorProvider(Color(it)) } ?: theme.outline,
        card = ColorProvider(cardColor),
        accentSoft = ColorProvider(softColor),
        onAccentSoft = ColorProvider(if (contrast(softColor, accentColor) >= MIN_TEXT_CONTRAST) accentColor else bestOn(softColor)),
        onSent = ColorProvider(bestOn(sentColor)),
        onOffline = ColorProvider(bestOn(offlineColor)),
    )
}

internal const val MIN_TEXT_CONTRAST = 4.5f

/** WCAG contrast ratio between two opaque colors. */
internal fun contrast(a: Color, b: Color): Float {
    val (hi, lo) = a.luminance().let { la -> b.luminance().let { lb -> maxOf(la, lb) to minOf(la, lb) } }
    return (hi + 0.05f) / (lo + 0.05f)
}

/** Black or white, whichever reads better on [background]. */
internal fun bestOn(background: Color): Color =
    if (contrast(background, Color.Black) >= contrast(background, Color.White)) Color.Black else Color.White

/** Secondary text: a softened [bestOn], pulled back to full strength if softening would drop below 4.5:1. */
internal fun softText(background: Color): Color {
    val full = bestOn(background)
    val soft = lerp(background, full, 0.75f)
    return if (contrast(background, soft) >= MIN_TEXT_CONTRAST) soft else full
}
