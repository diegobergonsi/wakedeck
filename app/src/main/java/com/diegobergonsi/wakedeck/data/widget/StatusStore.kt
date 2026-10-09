package com.diegobergonsi.wakedeck.data.widget

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import com.diegobergonsi.wakedeck.widget.WakeRecord
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.statusDataStore by preferencesDataStore("status_cache")

/** Last known device status and last wake result. Widgets only read this; workers and wake taps write it. */
@Singleton
class StatusStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val store get() = context.statusDataStore

    private fun statusKey(id: Long) = stringPreferencesKey("status.$id")
    private fun wakeKey(id: Long) = stringPreferencesKey("wake.$id")

    suspend fun setStatus(id: Long, status: DeviceStatus) {
        store.edit { it[statusKey(id)] = status.name }
    }

    suspend fun status(id: Long): DeviceStatus? =
        store.data.first()[statusKey(id)]?.let { name -> DeviceStatus.entries.firstOrNull { it.name == name } }

    fun statusFlow(id: Long): Flow<DeviceStatus?> = store.data
        .map { prefs -> prefs[statusKey(id)]?.let { name -> DeviceStatus.entries.firstOrNull { it.name == name } } }
        .distinctUntilChanged()

    fun wakeFlow(id: Long): Flow<WakeRecord?> = store.data.map { prefs -> parseWake(prefs[wakeKey(id)]) }.distinctUntilChanged()

    suspend fun clearWake(id: Long) {
        store.edit { it.remove(wakeKey(id)) }
    }

    private fun parseWake(raw: String?): WakeRecord? {
        val parts = raw?.split('|') ?: return null
        val ts = parts.getOrNull(0)?.toLongOrNull() ?: return null
        return WakeRecord(ts, parts.getOrNull(1) == "true")
    }

    suspend fun recordWake(id: Long, success: Boolean, nowMs: Long = System.currentTimeMillis()) {
        store.edit { it[wakeKey(id)] = "$nowMs|$success" }
    }

    suspend fun lastWake(id: Long): WakeRecord? = parseWake(store.data.first()[wakeKey(id)])

    suspend fun remove(id: Long) {
        store.edit { it.remove(statusKey(id)); it.remove(wakeKey(id)) }
    }
}
