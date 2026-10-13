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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.diegobergonsi.wakedeck.MainActivity
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlin.math.ceil

/** Below this tile width the status label would be cut, so the tile drops its dot and tightens its padding. */
private const val NARROW_TILE_DP = 84f

/** Least height the tiles need; a grid shorter than this plus the 48 dp header has no header. */
private const val MIN_BODY_DP = 76f

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
                .roundedBackground(config.cornerRadiusDp, palette.background, palette.translucent)
                .clickable(openApp, R.drawable.ripple_none),
        ) {
            BorderOverlay(config, palette)
            Box(GlanceModifier.fillMaxSize().padding(4.dp)) {
            if (state.config == null || state.rows.isEmpty()) {
                WidgetEmptyState(state.config == null, palette, scale)
            } else {
                // Refresh lives in a header row (like the list); a grid too short for one simply has no refresh button.
                val touch = TOUCH_TARGET.value
                val headerFits = size.height.value - 8 - touch >= MIN_BODY_DP
                val titled = config.label.isNotBlank() && headerFits
                val withHeader = titled || (config.showRefresh && headerFits)
                val shown = when {
                    withHeader && config.label.isBlank() -> config.copy(label = context.getString(R.string.config_devices))
                    withHeader -> config
                    else -> config.copy(label = "", showRefresh = false)
                }
                // The tiles sit in an inner card with 4 dp padding, like the list.
                val usableHeight = size.height.value - 8 - 8 - if (withHeader) touch else 0f
                val usableWidth = size.width.value - 8 - 8
                val columns = if (config.gridColumns > 0) {
                    GridLayout.columnsFor(usableWidth, config.gridColumns)
                } else {
                    GridLayout.autoColumns(usableWidth, usableHeight, state.rows.size)
                }.coerceAtMost(state.rows.size)
                val tileHeight = GridLayout.tileHeightDp(usableHeight, ceil(state.rows.size / columns.toFloat()).toInt()).dp
                // LocalSize can read wider than the drawn widget (seen on a foldable), so 3+ columns counts as narrow too.
                val narrow = columns >= 3 || usableWidth / columns < NARROW_TILE_DP
                MultiBody(
                    shown, state, palette, scale, refreshInColumn = false, centerContent = true, preview = preview, headerIcon = true,
                ) {
                    val card = GlanceModifier
                        .fillMaxWidth()
                        .roundedBackground((config.cornerRadiusDp - 8).coerceAtLeast(4), palette.card, palette.translucent)
                        .padding(4.dp)
                    Box(if (preview) card else card.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (preview) {
                            Column(GlanceModifier.fillMaxWidth()) {
                                state.rows.chunked(columns).forEach { chunk ->
                                    Row(GlanceModifier.fillMaxWidth()) {
                                        chunk.forEach { row ->
                                            Box(GlanceModifier.defaultWeight()) { GridTile(row, config, palette, scale, columns, tileHeight, narrow) }
                                        }
                                        repeat(columns - chunk.size) { Spacer(GlanceModifier.defaultWeight()) }
                                    }
                                }
                            }
                        } else {
                            LazyVerticalGrid(GridCells.Fixed(columns), GlanceModifier.fillMaxWidth()) {
                                items(state.rows, itemId = { it.device.id }) { row ->
                                    GridTile(row, config, palette, scale, columns, tileHeight, narrow)
                                }
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
    narrow: Boolean,
) {
    val context = LocalContext.current
    val spec = WakeButtonSpec(row.device.id, row.feedback, palette, config)
    val wake = actionRunCallback<WakeAction>(actionParametersOf(DeviceIdKey to row.device.id))
    // A long localized status wraps to a second line when the tile is tall enough; otherwise it is cut with an ellipsis.
    val captionLines = if (tileHeight.value >= 76f * scale) 2 else 1
    Box(GlanceModifier.fillMaxWidth().padding(2.dp)) {
        val tile = GlanceModifier
            .fillMaxWidth()
            .height(tileHeight - 4.dp)
            .roundedBackground((config.cornerRadiusDp - 12).coerceIn(4, 12), buttonColor(spec))
            .clickable(wake, R.drawable.ripple_rounded)
            .padding(horizontal = if (narrow) 2.dp else 6.dp)
        val icon: @Composable () -> Unit = {
            Image(
                provider = ImageProvider(config.icon.drawableRes()),
                contentDescription = null,
                modifier = GlanceModifier.size((22 * scale).dp),
                colorFilter = ColorFilter.tint(buttonTextColor(spec)),
            )
        }
        val name: @Composable () -> Unit = {
            Text(
                row.device.name,
                maxLines = 1,
                style = TextStyle(color = buttonTextColor(spec), fontSize = ((if (narrow) 12 else 13) * scale).sp, fontWeight = FontWeight.Bold),
            )
        }
        val caption: @Composable () -> Unit = {
            when (row.feedback) {
                WakeFeedback.SENT -> TileCaption(context.getString(R.string.widget_sent), buttonTextColor(spec), scale, captionLines)
                WakeFeedback.FAILED -> TileCaption(context.getString(R.string.widget_failed), buttonTextColor(spec), scale, captionLines)
                WakeFeedback.NONE -> if (config.showStatus) {
                    // The label always stays (never color alone); the dot is dropped when the tile is too narrow for both.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!narrow) {
                            StatusDot(row.status, palette)
                            Spacer(GlanceModifier.width(4.dp))
                        }
                        TileCaption(statusLabel(context, row.status), buttonTextColor(spec), scale, captionLines, center = narrow && columns > 1)
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
private fun TileCaption(text: String, color: ColorProvider, scale: Float, maxLines: Int, center: Boolean = false) {
    Text(
        text,
        maxLines = maxLines,
        modifier = if (center) GlanceModifier.fillMaxWidth() else GlanceModifier,
        style = TextStyle(color = color, fontSize = (11 * scale).sp, textAlign = if (center) TextAlign.Center else null),
    )
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
