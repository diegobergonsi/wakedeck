package com.diegobergonsi.wakedeck.tile

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tileDataStore by preferencesDataStore("tile_devices")

/** Which device each Quick Settings tile slot wakes. */
@Singleton
class TileStore @Inject constructor(@ApplicationContext private val context: Context) {
    private fun key(slot: Int) = longPreferencesKey("tile.$slot")

    suspend fun deviceId(slot: Int): Long? = context.tileDataStore.data.first()[key(slot)]

    fun deviceIdFlow(slot: Int): Flow<Long?> = context.tileDataStore.data.map { it[key(slot)] }.distinctUntilChanged()

    suspend fun set(slot: Int, deviceId: Long?) {
        context.tileDataStore.edit { if (deviceId == null) it.remove(key(slot)) else it[key(slot)] = deviceId }
    }
}
