package com.diegobergonsi.wakedeck.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :id")
    fun observe(id: Long): Flow<DeviceEntity?>

    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun get(id: Long): DeviceEntity?

    @Upsert
    suspend fun upsert(device: DeviceEntity)

    @Query("UPDATE devices SET groupId = :groupId WHERE id IN (:ids)")
    suspend fun setGroup(ids: List<Long>, groupId: Long?)

    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun delete(id: Long)
}
