package com.diegobergonsi.wakedeck.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM device_groups ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM device_groups ORDER BY name COLLATE NOCASE")
    suspend fun getAll(): List<GroupEntity>

    @Upsert
    suspend fun upsert(group: GroupEntity): Long

    @Query("DELETE FROM device_groups WHERE id = :id")
    suspend fun deleteGroup(id: Long)

    /** Devices of a deleted group stay, they just become ungrouped. */
    @Query("UPDATE devices SET groupId = NULL WHERE groupId = :groupId")
    suspend fun clearGroupOfDevices(groupId: Long)

    @Transaction
    suspend fun delete(id: Long) {
        clearGroupOfDevices(id)
        deleteGroup(id)
    }
}
