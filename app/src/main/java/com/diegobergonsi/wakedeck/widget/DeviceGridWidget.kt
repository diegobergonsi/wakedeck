package com.diegobergonsi.wakedeck.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.diegobergonsi.wakedeck.MainActivity
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlin.math.ceil

/** Several devices as tiles. Tapping a tile wakes that device. */
class DeviceGridWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val initial = MultiWidgetStates.load(context, widgetId)
        val live = MultiWidgetStates.flow(context, widgetId)
        // Scrolling collections cannot render inside the config screen's preview, so it gets a plain layout.
        val draftFlow = WidgetDraft.flow(widgetId)
        provideContent {
            val state by live.collectAsState(initial)
            val draft by draftFlow.collectAsState()
            DeviceGridContent(state, preview = draft != null)
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            CompositionLocalProvider(LocalSize provides DpSize(200.dp, 80.dp)) {
                // The picker may lay a preview out without a size, so give it the real default size.
                Box(GlanceModifier.size(200.dp, 80.dp)) { DeviceGridContent(SampleWidgetStates.multi(count = 2), preview = true) }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        context.widgetEntryPoint().configStore().delete(GlanceAppWidgetManager(context).getAppWidgetId(glanceId))
    }
}

@Composable
fun DeviceGridContent(state: MultiWidgetState, preview: Boolean = false) {
    val config = state.config ?: WidgetConfig()
    WidgetTheme(config.themeMode) {
        val palette = widgetPalette(config)
        val context = LocalContext.current
        val scale = config.textSize.scale
        val size = LocalSize.current
        val openApp = actionStartActivity(Intent(context, MainActivity::class.java))

        Box(
            GlanceModifier
                .fillMaxSize()
                .roundedBackground(config.cornerRadiusDp, palette.background)
                .clickable(openApp, R.drawable.ripple_none),
        ) {
            BorderOverlay(config, palette)
            Box(GlanceModifier.fillMaxSize().padding(4.dp)) {
            if (state.config == null || state.rows.isEmpty()) {
                Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        context.getString(if (state.config == null) R.string.widget_not_configured else R.string.widget_device_missing),
                        style = TextStyle(color = palette.text, fontSize = (13 * scale).sp),
                    )
                }
            } else {
                val titled = config.label.isNotBlank()
                val usableWidth = size.width.value - 8 - if (titled) 0 else 28
                val usableHeight = size.height.value - 8 - if (titled) 36 else 0
                val columns = if (config.gridColumns > 0) {
                    GridLayout.columnsFor(usableWidth, config.gridColumns)
                } else {
                    GridLayout.autoColumns(usableWidth, usableHeight, state.rows.size)
                }.coerceAtMost(state.rows.size)
                val tileHeight = GridLayout.tileHeightDp(usableHeight, ceil(state.rows.size / columns.toFloat()).toInt()).dp
                MultiBody(config, state, palette, scale, refreshInColumn = true, centerContent = true, preview = preview) {
                    if (preview) {
                        Column(GlanceModifier.fillMaxWidth()) {
                            state.rows.chunked(columns).forEach { chunk ->
                                Row(GlanceModifier.fillMaxWidth()) {
                                    chunk.forEach { row ->
                                        Box(GlanceModifier.defaultWeight()) { GridTile(row, config, palette, scale, columns, tileHeight) }
                                    }
                                    repeat(columns - chunk.size) { Spacer(GlanceModifier.defaultWeight()) }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(GridCells.Fixed(columns), GlanceModifier.fillMaxWidth()) {
                            items(state.rows, itemId = { it.device.id }) { row ->
                                GridTile(row, config, palette, scale, columns, tileHeight)
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun GridTile(
    row: DeviceRowState,
    config: WidgetConfig,
    palette: WidgetPalette,
    scale: Float,
    columns: Int,
    tileHeight: Dp,
) {
    val context = LocalContext.current
    val spec = WakeButtonSpec(row.device.id, row.feedback, palette, config)
    val wake = actionRunCallback<WakeAction>(actionParametersOf(DeviceIdKey to row.device.id))
    Box(GlanceModifier.fillMaxWidth().padding(2.dp)) {
        val tile = GlanceModifier
            .fillMaxWidth()
            .height(tileHeight - 4.dp)
            .roundedBackground(minOf(config.cornerRadiusDp, 16), buttonColor(spec))
            .clickable(wake, R.drawable.ripple_rounded)
            .padding(horizontal = 6.dp)
        val icon: @Composable () -> Unit = {
            Image(
                provider = ImageProvider(config.icon.drawableRes()),
                contentDescription = null,
                modifier = GlanceModifier.size((22 * scale).dp),
                colorFilter = ColorFilter.tint(palette.onAccent),
            )
        }
        val name: @Composable () -> Unit = {
            Text(
                row.device.name,
                maxLines = 1,
                style = TextStyle(color = palette.onAccent, fontSize = (13 * scale).sp, fontWeight = FontWeight.Bold),
            )
        }
        val caption: @Composable () -> Unit = {
            when (row.feedback) {
                WakeFeedback.SENT -> TileCaption(context.getString(R.string.widget_sent), palette, scale)
                WakeFeedback.FAILED -> TileCaption(context.getString(R.string.widget_failed), palette, scale)
                WakeFeedback.NONE -> if (config.showStatus) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(row.status, palette)
                        Spacer(GlanceModifier.width(4.dp))
                        TileCaption(statusLabel(context, row.status), palette, scale)
                    }
                }
            }
        }
        if (columns == 1) {
            Row(tile, verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(GlanceModifier.width(8.dp))
                Column { name(); caption() }
            }
        } else {
            Column(tile, verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
                icon(); name(); caption()
            }
        }
    }
}

@Composable
private fun TileCaption(text: String, palette: WidgetPalette, scale: Float) {
    Text(text, maxLines = 1, style = TextStyle(color = palette.onAccent, fontSize = (11 * scale).sp))
}

private fun statusLabel(context: Context, status: DeviceStatus): String = context.getString(
    when (status) {
        DeviceStatus.ONLINE -> R.string.status_online
        DeviceStatus.OFFLINE -> R.string.status_offline
        DeviceStatus.CHECKING -> R.string.status_checking
        DeviceStatus.UNAVAILABLE -> R.string.status_no_host
        DeviceStatus.STALE -> R.string.status_stale
    },
)
