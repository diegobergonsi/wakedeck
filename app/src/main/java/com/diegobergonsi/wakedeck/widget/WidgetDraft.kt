package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory unsaved config used by the live preview in the config screen. It is a flow so a widget session that
 * is already running follows it while editing and falls back to the stored config once the draft is cleared.
 */
object WidgetDraft {
    private val drafts = ConcurrentHashMap<Int, MutableStateFlow<WidgetConfig?>>()

    private fun holder(widgetId: Int) = drafts.getOrPut(widgetId) { MutableStateFlow(null) }

    fun flow(widgetId: Int): StateFlow<WidgetConfig?> = holder(widgetId)
    fun get(widgetId: Int): WidgetConfig? = holder(widgetId).value
    fun set(widgetId: Int, config: WidgetConfig) { holder(widgetId).value = config }
    fun clear(widgetId: Int) { holder(widgetId).value = null }
}
