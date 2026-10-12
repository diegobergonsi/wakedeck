package com.diegobergonsi.wakedeck.widget

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.diegobergonsi.wakedeck.MainActivity
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig

/** Minimum touch target size (Android accessibility guidance). */
val TOUCH_TARGET = 48.dp

/** Small refresh icon shared by all widget types. [params] say which device(s) to re-check. */
@Composable
fun RefreshIconButton(
    params: ActionParameters,
    palette: WidgetPalette,
    config: WidgetConfig,
    size: Dp = TOUCH_TARGET,
) {
    if (!config.showRefresh) return
    // The tap area is the full 48 dp; the icon inside stays small.
    Box(
        GlanceModifier.size(size).clickable(actionRunCallback<RefreshAction>(params), R.drawable.ripple_circle),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_refresh),
            contentDescription = LocalContext.current.getString(R.string.widget_refresh),
            modifier = GlanceModifier.size(22.dp),
            colorFilter = ColorFilter.tint(palette.subtext),
        )
    }
}

@Composable
fun StatusDot(status: DeviceStatus, palette: WidgetPalette) {
    val color = when (status) {
        DeviceStatus.ONLINE -> palette.online
        DeviceStatus.OFFLINE -> palette.offline
        DeviceStatus.CHECKING, DeviceStatus.UNAVAILABLE, DeviceStatus.STALE -> palette.neutral
    }
    Box(GlanceModifier.size(10.dp).dotBackground(color)) {}
}

/**
 * Body of the list and grid widgets. With a title: a header row (title + refresh) above the content.
 * Without one, a header row still appears when [refreshInColumn] is false and refresh is on, so refresh never floats loose.
 */
@Composable
fun MultiBody(
    config: WidgetConfig,
    state: MultiWidgetState,
    palette: WidgetPalette,
    scale: Float,
    refreshInColumn: Boolean,
    centerContent: Boolean,
    preview: Boolean = false,
    headerIcon: Boolean = false,
    content: @Composable () -> Unit,
) {
    val titled = config.label.isNotBlank()
    val header = titled || (!refreshInColumn && config.showRefresh)
    val refresh = actionParametersOf(DeviceIdsKey to state.rows.joinToString(",") { it.device.id.toString() })
    Column(GlanceModifier.fillMaxSize()) {
        if (header) {
            Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (headerIcon) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_power),
                        contentDescription = null,
                        modifier = GlanceModifier.padding(start = 4.dp).size(20.dp),
                        colorFilter = ColorFilter.tint(palette.accent),
                    )
                }
                Text(
                    config.label,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight().padding(start = if (headerIcon) 8.dp else 4.dp),
                    style = TextStyle(color = palette.text, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold),
                )
                RefreshIconButton(refresh, palette, config)
            }
        }
        Row(
            // Previews have no scrolling collection to stretch, so the row just wraps its content.
            if (preview) GlanceModifier.fillMaxWidth() else GlanceModifier.fillMaxSize(),
            verticalAlignment = if (centerContent) Alignment.CenterVertically else Alignment.Top,
        ) {
            Box(GlanceModifier.defaultWeight()) { content() }
        }
    }
}

/** Shown when the widget has nothing to show: the message, plus an "Add device" button when the widget is tall enough. */
@Composable
fun WidgetEmptyState(notConfigured: Boolean, palette: WidgetPalette, scale: Float) {
    val context = LocalContext.current
    val message = context.getString(if (notConfigured) R.string.widget_not_configured else R.string.widget_device_missing)
    val textStyle = TextStyle(color = palette.text, fontSize = (13 * scale).sp, textAlign = TextAlign.Center)
    Column(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, maxLines = 2, modifier = GlanceModifier.fillMaxWidth(), style = textStyle)
        // A 2x1 has no room for a 48 dp button under the message.
        if (LocalSize.current.height >= 110.dp) {
            Box(
                GlanceModifier.height(TOUCH_TARGET).clickable(actionStartActivity(Intent(context, MainActivity::class.java)), R.drawable.ripple_rounded),
                contentAlignment = Alignment.Center,
            ) {
                Box(GlanceModifier.roundedBackground(20, palette.accent).padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        context.getString(R.string.add_device),
                        maxLines = 1,
                        style = TextStyle(color = palette.onAccent, fontSize = (13 * scale).sp, fontWeight = FontWeight.Medium),
                    )
                }
            }
        }
    }
}
