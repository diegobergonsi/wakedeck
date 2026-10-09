package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory unsaved config used by the live preview in the config screen. Only the app process sees it,
 * and the config screen clears it when it closes.
 */
object WidgetDraft {
    private val drafts = ConcurrentHashMap<Int, WidgetConfig>()

    fun get(widgetId: Int): WidgetConfig? = drafts[widgetId]
    fun set(widgetId: Int, config: WidgetConfig) { drafts[widgetId] = config }
    fun clear(widgetId: Int) { drafts.remove(widgetId) }
}
