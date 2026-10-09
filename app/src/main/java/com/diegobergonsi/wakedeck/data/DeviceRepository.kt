package com.diegobergonsi.wakedeck.data

import com.diegobergonsi.wakedeck.data.db.DeviceDao
import com.diegobergonsi.wakedeck.data.db.toDomain
import com.diegobergonsi.wakedeck.data.db.toEntity
import com.diegobergonsi.wakedeck.domain.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(private val dao: DeviceDao) {
    fun observeAll(): Flow<List<Device>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun get(id: Long): Device? = dao.get(id)?.toDomain()

    suspend fun save(device: Device) = dao.upsert(device.toEntity())

    suspend fun delete(id: Long) = dao.delete(id)
}
