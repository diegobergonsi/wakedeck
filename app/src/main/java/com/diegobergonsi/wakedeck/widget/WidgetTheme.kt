package com.diegobergonsi.wakedeck.widget

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
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
)

private val OnlineGreen = Color(0xFF2E9E4F)
private val OfflineRed = Color(0xFFD64545)

@Composable
fun WidgetTheme(mode: WidgetThemeMode, content: @Composable () -> Unit) {
    when (mode) {
        WidgetThemeMode.MATERIAL_YOU -> GlanceTheme(content = content)
        WidgetThemeMode.LIGHT -> GlanceTheme(colors = ColorProviders(lightColorScheme()), content = content)
        WidgetThemeMode.DARK -> GlanceTheme(colors = ColorProviders(darkColorScheme()), content = content)
    }
}

/** Must be called inside [WidgetTheme]. Custom colors win over the theme; transparency applies to the background. */
@Composable
fun widgetPalette(config: WidgetConfig): WidgetPalette {
    val theme = GlanceTheme.colors
    val context = LocalContext.current

    val customBg = config.backgroundColor?.let { Color(it) }
    val bgColor = (customBg ?: theme.widgetBackground.getColor(context)).copy(alpha = config.backgroundAlpha)
    val text = if (customBg != null) contrastOn(customBg) else theme.onSurface
    val subtext = if (customBg != null) contrastOn(customBg, 0.7f) else theme.onSurfaceVariant

    val customAccent = config.accentColor?.let { Color(it) }
    return WidgetPalette(
        background = ColorProvider(bgColor),
        text = text,
        subtext = subtext,
        accent = customAccent?.let { ColorProvider(it) } ?: theme.primary,
        onAccent = customAccent?.let { contrastOn(it) } ?: theme.onPrimary,
        online = ColorProvider(config.onlineColor?.let { Color(it) } ?: OnlineGreen),
        offline = ColorProvider(config.offlineColor?.let { Color(it) } ?: OfflineRed),
        neutral = theme.outline,
        sent = ColorProvider(config.sentColor?.let { Color(it) } ?: config.onlineColor?.let { Color(it) } ?: OnlineGreen),
        border = config.borderColor?.let { ColorProvider(Color(it)) } ?: theme.outline,
    )
}

private fun contrastOn(background: Color, alpha: Float = 1f): ColorProvider =
    ColorProvider((if (background.luminance() > 0.5f) Color.Black else Color.White).copy(alpha = alpha))
