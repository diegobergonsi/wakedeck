package com.diegobergonsi.wakedeck.data.widget

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import com.diegobergonsi.wakedeck.widget.config.WidgetConfigCodec
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetConfigDataStore by preferencesDataStore("widget_config")

/** Keys are `cfg.<appWidgetId>.<field>`, so new fields never need a migration. */
@Singleton
class WidgetConfigStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val store get() = context.widgetConfigDataStore

    private fun key(widgetId: Int, field: String) = stringPreferencesKey("cfg.$widgetId.$field")

    suspend fun get(widgetId: Int): WidgetConfig? = decode(widgetId, store.data.first())

    fun flow(widgetId: Int): Flow<WidgetConfig?> = store.data.map { decode(widgetId, it) }.distinctUntilChanged()

    private fun decode(widgetId: Int, prefs: androidx.datastore.preferences.core.Preferences): WidgetConfig? {
        val map = WidgetConfigCodec.fields.mapNotNull { f -> prefs[key(widgetId, f)]?.let { f to it } }.toMap()
        return if (map.isEmpty()) null else WidgetConfigCodec.decode(map)
    }

    suspend fun save(widgetId: Int, config: WidgetConfig) {
        val encoded = WidgetConfigCodec.encode(config)
        store.edit { prefs ->
            WidgetConfigCodec.fields.forEach { f ->
                val v = encoded[f]
                if (v == null) prefs.remove(key(widgetId, f)) else prefs[key(widgetId, f)] = v
            }
        }
    }

    suspend fun delete(widgetId: Int) {
        store.edit { prefs -> WidgetConfigCodec.fields.forEach { prefs.remove(key(widgetId, it)) } }
    }

    /** Drops stored configs of widgets that no longer exist (for example if a delete was missed). */
    suspend fun pruneExcept(activeWidgetIds: Set<Int>) {
        val prefs = store.data.first()
        val stored = prefs.asMap().keys.mapNotNull { it.name.removePrefix("cfg.").substringBefore('.').toIntOrNull() }.toSet()
        (stored - activeWidgetIds).forEach { delete(it) }
    }

    /** Ids of all devices referenced by any stored widget. */
    suspend fun allDeviceIds(): Set<Long> {
        val prefs = store.data.first()
        return prefs.asMap().entries
            .filter { it.key.name.startsWith("cfg.") && it.key.name.endsWith(".deviceIds") }
            .flatMap { (it.value as String).split(',').mapNotNull { s -> s.trim().toLongOrNull() } }
            .toSet()
    }
}
