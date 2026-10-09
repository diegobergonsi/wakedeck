package com.diegobergonsi.wakedeck.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig

/** Small refresh icon shared by all widget types. [params] say which device(s) to re-check. */
@Composable
fun RefreshIconButton(
    params: ActionParameters,
    palette: WidgetPalette,
    config: WidgetConfig,
    size: Dp = 36.dp,
) {
    if (!config.showRefresh) return
    Box(
        GlanceModifier.size(size).clickable(actionRunCallback<RefreshAction>(params), R.drawable.ripple_circle),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_refresh),
            contentDescription = LocalContext.current.getString(R.string.widget_refresh),
            modifier = GlanceModifier.size(size * 0.62f),
            colorFilter = ColorFilter.tint(palette.subtext),
        )
    }
}

@Composable
fun StatusDot(status: DeviceStatus, palette: WidgetPalette) {
    val color = when (status) {
        DeviceStatus.ONLINE -> palette.online
        DeviceStatus.OFFLINE -> palette.offline
        DeviceStatus.CHECKING, DeviceStatus.UNAVAILABLE -> palette.neutral
    }
    Box(GlanceModifier.size(10.dp).dotBackground(color)) {}
}

/**
 * Body of the list and grid widgets. With a title: a header row (title + refresh) above the content.
 * Without one: the grid puts the refresh icon in a thin column on the right ([refreshInColumn]) so no height is
 * wasted; the list uses a thin header row so its Wake buttons stay flush with the right edge.
 */
@Composable
fun MultiBody(
    config: WidgetConfig,
    state: MultiWidgetState,
    palette: WidgetPalette,
    scale: Float,
    refreshInColumn: Boolean,
    centerContent: Boolean,
    content: @Composable () -> Unit,
) {
    val titled = config.label.isNotBlank()
    val header = titled || (!refreshInColumn && config.showRefresh)
    val refresh = actionParametersOf(DeviceIdsKey to state.rows.joinToString(",") { it.device.id.toString() })
    Column(GlanceModifier.fillMaxSize()) {
        if (header) {
            Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    config.label,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight().padding(start = 4.dp),
                    style = TextStyle(color = palette.text, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold),
                )
                RefreshIconButton(refresh, palette, config, size = if (titled) 36.dp else 28.dp)
            }
        }
        Row(
            GlanceModifier.fillMaxSize(),
            verticalAlignment = if (centerContent) Alignment.CenterVertically else Alignment.Top,
        ) {
            Box(GlanceModifier.defaultWeight()) { content() }
            if (!header) RefreshIconButton(refresh, palette, config, size = 28.dp)
        }
    }
}
