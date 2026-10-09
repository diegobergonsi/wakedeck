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

/** A cached status and when it was checked (null for values written by older builds). */
data class StatusSnapshot(val status: DeviceStatus, val checkedAtMs: Long?) {
    /** ONLINE/OFFLINE older than [STALE_AFTER_MS] become STALE, so an old green or red is not shown as current. */
    fun effective(nowMs: Long): DeviceStatus {
        val checked = checkedAtMs ?: return status
        val known = status == DeviceStatus.ONLINE || status == DeviceStatus.OFFLINE
        return if (known && nowMs - checked > STALE_AFTER_MS) DeviceStatus.STALE else status
    }

    companion object {
        const val STALE_AFTER_MS = 45 * 60 * 1000L
    }
}

/** Last known device status and last wake result. Widgets only read this; workers and wake taps write it. */
@Singleton
class StatusStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val store get() = context.statusDataStore

    private fun statusKey(id: Long) = stringPreferencesKey("status.$id")
    private fun wakeKey(id: Long) = stringPreferencesKey("wake.$id")

    suspend fun setStatus(id: Long, status: DeviceStatus, nowMs: Long = System.currentTimeMillis()) {
        store.edit { it[statusKey(id)] = "${status.name}|$nowMs" }
    }

    suspend fun status(id: Long): StatusSnapshot? = parseStatus(store.data.first()[statusKey(id)])

    private fun parseStatus(raw: String?): StatusSnapshot? {
        val parts = raw?.split('|') ?: return null
        val status = DeviceStatus.entries.firstOrNull { it.name == parts[0] } ?: return null
        return StatusSnapshot(status, parts.getOrNull(1)?.toLongOrNull())
    }

    fun statusFlow(id: Long): Flow<StatusSnapshot?> = store.data
        .map { prefs -> parseStatus(prefs[statusKey(id)]) }
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
