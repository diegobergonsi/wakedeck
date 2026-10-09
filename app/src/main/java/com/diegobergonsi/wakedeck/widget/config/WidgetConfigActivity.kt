package com.diegobergonsi.wakedeck.widget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.diegobergonsi.wakedeck.ui.theme.WakeDeckTheme
import dagger.hilt.android.AndroidEntryPoint

/** Opens when a widget is added and again on "reconfigure". */
@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {
    private val viewModel: WidgetConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        setResult(Activity.RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        viewModel.start(widgetId)
        enableEdgeToEdge()
        setContent {
            WakeDeckTheme {
                WidgetConfigScreen(
                    viewModel = viewModel,
                    widgetId = widgetId,
                    onCancel = { finish() },
                    onSaved = {
                        setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                        finish()
                    },
                )
            }
        }
    }
}
