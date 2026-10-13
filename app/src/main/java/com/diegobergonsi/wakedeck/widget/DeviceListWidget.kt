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
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.size
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.diegobergonsi.wakedeck.MainActivity
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.di.widgetEntryPoint
import com.diegobergonsi.wakedeck.widget.config.ListRowStyle
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig

private const val CARD_PADDING_DP = 2f

/** Several devices in a scrolling list, each with its own Wake button. */
class DeviceListWidget : GlanceAppWidget() {
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
            DeviceListContent(state, preview = draft != null)
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            CompositionLocalProvider(LocalSize provides DpSize(220.dp, 150.dp)) {
                // The picker may lay a preview out without a size, so give it the real default size.
                Box(GlanceModifier.size(220.dp, 150.dp)) { DeviceListContent(SampleWidgetStates.multi(count = 2), preview = true) }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        context.widgetEntryPoint().configStore().delete(GlanceAppWidgetManager(context).getAppWidgetId(glanceId))
    }
}

@Composable
fun DeviceListContent(state: MultiWidgetState, preview: Boolean = false) {
    val context = LocalContext.current
    // The list always has a title row (it carries the refresh button), so an unset title falls back to "Devices".
    val saved = state.config ?: WidgetConfig()
    val config = if (saved.label.isBlank()) saved.copy(label = context.getString(R.string.config_devices)) else saved
    WidgetTheme(config.themeMode) {
        val palette = widgetPalette(config)
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
            Box(GlanceModifier.fillMaxSize().padding(8.dp)) {
            if (state.config == null || state.rows.isEmpty()) {
                WidgetEmptyState(state.config == null, palette, scale)
            } else {
                val headerHeight = TOUCH_TARGET.value.toInt()
                // The rows sit in their own card; refresh and title stay outside it, on the widget background. Its corners are
                // concentric with the widget's (outer radius minus the 8 dp padding), as Material widget guidance asks.
                val cardHeight = size.height.value - 8 - headerHeight
                val rowHeight = ListLayout.rowHeightDp(cardHeight - CARD_PADDING_DP * 2, state.rows.size, config.density)
                val cardModifier = GlanceModifier
                    .fillMaxWidth()
                    .roundedBackground((config.cornerRadiusDp - 8).coerceAtLeast(4), palette.card, palette.translucent)
                    .padding(horizontal = 4.dp, vertical = CARD_PADDING_DP.dp)
                MultiBody(config, state, palette, scale, refreshInColumn = false, centerContent = false, preview = preview, headerIcon = true) {
                    // LocalSize can read smaller than the drawn widget, so the card fills the space rather than hugging rows.
                    Box(if (preview) cardModifier else cardModifier.fillMaxSize()) {
                        if (preview) {
                            Column(GlanceModifier.fillMaxWidth()) {
                                state.rows.forEach { row -> ListRow(row, config, palette, scale, rowHeight.dp) }
                            }
                        } else {
                            LazyColumn(GlanceModifier.fillMaxWidth()) {
                                items(state.rows, itemId = { it.device.id }) { row ->
                                    ListRow(row, config, palette, scale, rowHeight.dp)
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
private fun ListRow(row: DeviceRowState, config: WidgetConfig, palette: WidgetPalette, scale: Float, rowHeight: Dp) {
    val button = WakeButtonSpec(row.device.id, row.feedback, palette, config)
    Row(
        GlanceModifier.fillMaxWidth().height(rowHeight).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(GlanceModifier.defaultWeight()) {
            Text(
                row.device.name,
                maxLines = 1,
                style = TextStyle(color = palette.text, fontSize = (15 * scale).sp, fontWeight = FontWeight.Bold),
            )
            if (config.showStatus && config.rowStyle == ListRowStyle.DETAILED) {
                StatusLine(row.status, palette, scale, iconColumn = false, checkedAtMs = row.checkedAtMs)
            }
        }
        Spacer(GlanceModifier.width(8.dp))
        if (config.showStatus && config.rowStyle == ListRowStyle.MINIMAL) {
            StatusDot(row.status, palette)
            Spacer(GlanceModifier.width(8.dp))
        }
        WakeButton(button, dense = true)
    }
}
