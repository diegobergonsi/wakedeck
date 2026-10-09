package com.diegobergonsi.wakedeck.widget

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import java.text.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

// Layout tiers. Glance picks the largest of these that fits the real widget size.
private val Small = DpSize(60.dp, 60.dp) // icon button only
private val Compact = DpSize(110.dp, 60.dp) // icon button + name + status
private val CompactPlus = DpSize(150.dp, 60.dp) // same, plus the refresh icon
private val Wide = DpSize(200.dp, 60.dp) // icon + name + status + "Wake" button
private val Tall = DpSize(110.dp, 130.dp) // column
private val Large = DpSize(200.dp, 130.dp) // column, more room

/** What a widget needs to draw itself. Built from the stores, or from a draft while configuring. */
data class SingleWidgetState(
    val config: WidgetConfig?,
    val device: Device?,
    val status: DeviceStatus,
    val feedback: WakeFeedback,
    val checkedAtMs: Long? = null,
)

class SingleDeviceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(Small, Compact, CompactPlus, Wide, Tall, Large))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val initial = loadState(context, widgetId)
        // The composition stays alive across updates, so state must be observed here, not read once.
        val live = stateFlow(context, widgetId)
        provideContent {
            val state by live.collectAsState(initial)
            SingleDeviceContent(state)
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            CompositionLocalProvider(LocalSize provides DpSize(220.dp, 70.dp)) { SingleDeviceContent(SampleWidgetStates.single()) }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        context.widgetEntryPoint().configStore().delete(widgetId)
    }

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun stateFlow(context: Context, widgetId: Int): Flow<SingleWidgetState> {
            val entry = context.widgetEntryPoint()
            val configs = combine(WidgetDraft.flow(widgetId), entry.configStore().flow(widgetId)) { draft, stored -> draft ?: stored }
            return configs.flatMapLatest { config ->
                val deviceId = config?.deviceIds?.firstOrNull()
                if (deviceId == null) {
                    flowOf(SingleWidgetState(config, null, DeviceStatus.CHECKING, WakeFeedback.NONE))
                } else {
                    combine(
                        entry.deviceRepository().observe(deviceId),
                        entry.statusStore().statusFlow(deviceId),
                        entry.statusStore().wakeFlow(deviceId),
                    ) { device, snapshot, wake ->
                        SingleWidgetState(
                            config,
                            device,
                            snapshot?.effective(System.currentTimeMillis()) ?: DeviceStatus.CHECKING,
                            wake?.feedbackAt(System.currentTimeMillis()) ?: WakeFeedback.NONE,
                            snapshot?.checkedAtMs,
                        )
                    }
                }
            }
        }

        suspend fun loadState(context: Context, widgetId: Int): SingleWidgetState {
            val entry = context.widgetEntryPoint()
            val config = WidgetDraft.get(widgetId) ?: entry.configStore().get(widgetId)
            val device = config?.deviceIds?.firstOrNull()?.let { entry.deviceRepository().get(it) }
            val snapshot = device?.let { entry.statusStore().status(it.id) }
            val feedback = device?.let { entry.statusStore().lastWake(it.id)?.feedbackAt(System.currentTimeMillis()) }
                ?: WakeFeedback.NONE
            return SingleWidgetState(config, device, snapshot?.effective(System.currentTimeMillis()) ?: DeviceStatus.CHECKING, feedback, snapshot?.checkedAtMs)
        }
    }
}

@Composable
fun SingleDeviceContent(state: SingleWidgetState) {
    val config = state.config ?: WidgetConfig()
    WidgetTheme(config.themeMode) {
        val palette = widgetPalette(config)
        val context = LocalContext.current
        val size = LocalSize.current
        val scale = config.textSize.scale

        // A 1x1 slot is taller than wide on most launchers. There the button itself is the tile, no outer background.
        val iconOnly = size.width < Compact.width
        val openApp = actionStartActivity(Intent(context, MainActivity::class.java))
        val root = if (iconOnly) {
            GlanceModifier.fillMaxSize().clickable(openApp, R.drawable.ripple_none)
        } else {
            GlanceModifier
                .fillMaxSize()
                .roundedBackground(config.cornerRadiusDp, palette.background)
                .clickable(openApp, R.drawable.ripple_none)
        }

        val device = state.device
        if (state.config == null || device == null) {
            Box(root.padding(8.dp), contentAlignment = Alignment.Center) {
                Text(
                    context.getString(if (state.config == null) R.string.widget_not_configured else R.string.widget_device_missing),
                    style = TextStyle(color = palette.text, fontSize = (13 * scale).sp),
                )
            }
            return@WidgetTheme
        }

        val name = config.label.ifBlank { device.name }
        val button = WakeButtonSpec(device.id, state.feedback, palette, config)

        Box(root) {
            if (!iconOnly) BorderOverlay(config, palette)
            Box(GlanceModifier.fillMaxSize().padding(if (iconOnly) 4.dp else 8.dp), contentAlignment = Alignment.Center) {
                when {
                    iconOnly -> IconOnlyLayout(button)
                    size.height >= Tall.height -> ColumnLayout(name, state, button, palette, scale)
                    size.width < Wide.width -> CompactLayout(name, state, button, palette, scale, withRefresh = size.width >= CompactPlus.width)
                    else -> RowLayout(name, state, button, palette, scale)
                }
            }
        }
    }
}

internal class WakeButtonSpec(
    val deviceId: Long,
    val feedback: WakeFeedback,
    val palette: WidgetPalette,
    val config: WidgetConfig,
)

@Composable
internal fun buttonColor(spec: WakeButtonSpec) = when (spec.feedback) {
    WakeFeedback.SENT -> spec.palette.sent
    WakeFeedback.FAILED -> spec.palette.offline
    WakeFeedback.NONE -> spec.palette.accent
}

@Composable
private fun IconOnlyLayout(spec: WakeButtonSpec) {
    // Fixed square so the button keeps its shape in tall or wide slots instead of stretching into a pill.
    Box(
        GlanceModifier
            .size(52.dp)
            .roundedBackground(minOf(spec.config.cornerRadiusDp, 20), buttonColor(spec))
            .clickable(actionRunCallback<WakeAction>(actionParametersOf(DeviceIdKey to spec.deviceId)), R.drawable.ripple_rounded),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(spec.config.icon.drawableRes()),
            contentDescription = LocalContext.current.getString(R.string.wake),
            modifier = GlanceModifier.size(28.dp),
            colorFilter = ColorFilter.tint(spec.palette.onAccent),
        )
    }
}

@Composable
internal fun WakeButton(spec: WakeButtonSpec, modifier: GlanceModifier = GlanceModifier) {
    val context = LocalContext.current
    val label = when (spec.feedback) {
        WakeFeedback.SENT -> R.string.widget_sent
        WakeFeedback.FAILED -> R.string.widget_failed
        WakeFeedback.NONE -> R.string.wake
    }
    Box(
        modifier
            .roundedBackground(spec.config.cornerRadiusDp / 2, buttonColor(spec))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clickable(actionRunCallback<WakeAction>(actionParametersOf(DeviceIdKey to spec.deviceId)), R.drawable.ripple_rounded),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            context.getString(label),
            style = TextStyle(
                color = spec.palette.onAccent,
                fontSize = (14 * spec.config.textSize.scale).sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun NameAndStatus(name: String, state: SingleWidgetState, palette: WidgetPalette, scale: Float) {
    val config = state.config ?: return
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = ImageProvider(config.icon.drawableRes()),
                contentDescription = null,
                modifier = GlanceModifier.size((20 * scale).dp),
                colorFilter = ColorFilter.tint(palette.text),
            )
            Spacer(GlanceModifier.width(6.dp))
            Text(
                name,
                maxLines = 1,
                style = TextStyle(color = palette.text, fontSize = (16 * scale).sp, fontWeight = FontWeight.Bold),
            )
        }
        if (config.showStatus) {
            Spacer(GlanceModifier.height(4.dp))
            StatusLine(state.status, palette, scale, checkedAtMs = state.checkedAtMs)
        }
    }
}

@Composable
internal fun StatusLine(
    status: DeviceStatus,
    palette: WidgetPalette,
    scale: Float,
    iconColumn: Boolean = true,
    checkedAtMs: Long? = null,
) {
    val context = LocalContext.current
    val (color, label) = when (status) {
        DeviceStatus.ONLINE -> palette.online to R.string.status_online
        DeviceStatus.OFFLINE -> palette.offline to R.string.status_offline
        DeviceStatus.CHECKING -> palette.neutral to R.string.status_checking
        DeviceStatus.UNAVAILABLE -> palette.neutral to R.string.status_no_host
        DeviceStatus.STALE -> palette.neutral to R.string.status_stale
    }
    // Same two columns as the name row: the dot is centered under the icon, the text starts under the name.
    Row(verticalAlignment = Alignment.CenterVertically) {
        val dot = GlanceModifier.size(8.dp).dotBackground(color)
        if (iconColumn) {
            Box(GlanceModifier.width((20 * scale).dp), contentAlignment = Alignment.Center) { Box(dot) {} }
        } else {
            Box(dot) {}
        }
        Spacer(GlanceModifier.width(6.dp))
        // Clock time, not "x min ago": a widget cannot tick, so a relative age would freeze and mislead.
        val checked = checkedAtMs
            ?.takeIf { status == DeviceStatus.ONLINE || status == DeviceStatus.OFFLINE || status == DeviceStatus.STALE }
            ?.let { DateUtils.formatSameDayTime(it, System.currentTimeMillis(), DateFormat.SHORT, DateFormat.SHORT) }
        val text = if (checked != null) "${context.getString(label)} · $checked" else context.getString(label)
        Text(text, maxLines = 1, style = TextStyle(color = palette.subtext, fontSize = (12 * scale).sp))
    }
}

@Composable
private fun RowLayout(name: String, state: SingleWidgetState, button: WakeButtonSpec, palette: WidgetPalette, scale: Float) {
    Row(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(GlanceModifier.defaultWeight()) { NameAndStatus(name, state, palette, scale) }
        state.config?.let { RefreshButton(button.deviceId, palette, it) }
        Spacer(GlanceModifier.width(4.dp))
        WakeButton(button)
    }
}

@Composable
private fun ColumnLayout(name: String, state: SingleWidgetState, button: WakeButtonSpec, palette: WidgetPalette, scale: Float) {
    Column(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(GlanceModifier.defaultWeight()) { NameAndStatus(name, state, palette, scale) }
            state.config?.let { RefreshButton(button.deviceId, palette, it) }
        }
        Spacer(GlanceModifier.defaultWeight())
        WakeButton(button, GlanceModifier.fillMaxWidth())
    }
}

/** For a 2x1 slot: the icon is the Wake button, name and status sit next to it. */
@Composable
private fun CompactLayout(
    name: String,
    state: SingleWidgetState,
    button: WakeButtonSpec,
    palette: WidgetPalette,
    scale: Float,
    withRefresh: Boolean,
) {
    val config = state.config ?: return
    val context = LocalContext.current
    Row(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            GlanceModifier
                .size(44.dp)
                .roundedBackground(minOf(config.cornerRadiusDp, 14), buttonColor(button))
                .clickable(actionRunCallback<WakeAction>(actionParametersOf(DeviceIdKey to button.deviceId)), R.drawable.ripple_rounded),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(config.icon.drawableRes()),
                contentDescription = context.getString(R.string.wake),
                modifier = GlanceModifier.size(24.dp),
                colorFilter = ColorFilter.tint(palette.onAccent),
            )
        }
        Spacer(GlanceModifier.width(8.dp))
        Column(GlanceModifier.defaultWeight()) {
            Text(
                name,
                maxLines = 1,
                style = TextStyle(color = palette.text, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold),
            )
            when (button.feedback) {
                WakeFeedback.SENT -> Text(
                    context.getString(R.string.widget_sent),
                    style = TextStyle(color = palette.online, fontSize = (12 * scale).sp),
                )
                WakeFeedback.FAILED -> Text(
                    context.getString(R.string.widget_failed),
                    style = TextStyle(color = palette.offline, fontSize = (12 * scale).sp),
                )
                WakeFeedback.NONE -> if (config.showStatus) StatusLine(state.status, palette, scale, iconColumn = false)
            }
        }
        if (withRefresh) RefreshButton(button.deviceId, palette, config)
    }
}

@Composable
private fun RefreshButton(deviceId: Long, palette: WidgetPalette, config: WidgetConfig) =
    RefreshIconButton(actionParametersOf(DeviceIdKey to deviceId), palette, config)
