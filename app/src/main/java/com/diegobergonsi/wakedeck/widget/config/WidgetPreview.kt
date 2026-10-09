package com.diegobergonsi.wakedeck.widget.config

import android.content.ComponentName
import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.runComposition
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.widget.DeviceGridWidget
import com.diegobergonsi.wakedeck.widget.DeviceGridWidgetReceiver
import com.diegobergonsi.wakedeck.widget.DeviceListWidget
import com.diegobergonsi.wakedeck.widget.DeviceListWidgetReceiver
import com.diegobergonsi.wakedeck.widget.SingleDeviceWidget
import com.diegobergonsi.wakedeck.widget.SingleDeviceWidgetReceiver
import com.diegobergonsi.wakedeck.widget.WidgetType
import kotlinx.coroutines.flow.first

/**
 * Renders the real Glance widget into a view using the unsaved draft config. Taps are swallowed so the
 * preview cannot wake anything. If rendering fails, a text is shown and configuring still works.
 */
@OptIn(ExperimentalGlanceApi::class)
@Composable
fun WidgetPreview(widgetId: Int, config: WidgetConfig, type: WidgetType, size: DpSize, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var failed by remember { mutableStateOf(false) }
    val frame = remember { FrameLayout(context) }

    LaunchedEffect(config, size, type) {
        try {
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(widgetId)
            val (widget, receiver) = when (type) {
                WidgetType.SINGLE -> SingleDeviceWidget() to SingleDeviceWidgetReceiver::class.java
                WidgetType.LIST -> DeviceListWidget() to DeviceListWidgetReceiver::class.java
                WidgetType.GRID -> DeviceGridWidget() to DeviceGridWidgetReceiver::class.java
            }
            val views = widget.runComposition(
                context,
                glanceId,
                Bundle(),
                listOf(size),
                null,
                ComponentName(context, receiver),
            ).first()
            frame.removeAllViews()
            frame.addView(views.apply(context, frame))
            failed = false
        } catch (e: Exception) {
            failed = true
        }
    }

    Box(modifier.size(size.width, size.height)) {
        if (failed) {
            Text(stringResource(R.string.preview_unavailable), Modifier.align(Alignment.Center))
        } else {
            AndroidView(factory = { frame }, modifier = Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { } })
        }
    }
}
