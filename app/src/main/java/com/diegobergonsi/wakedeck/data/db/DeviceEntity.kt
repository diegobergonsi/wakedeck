package com.diegobergonsi.wakedeck.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.OsType

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mac: String,
    val host: String,
    val broadcastAddress: String,
    val port: Int,
    val os: String,
    val statusPort: Int?,
    val groupId: Long?,
)

fun DeviceEntity.toDomain() = Device(
    id = id,
    name = name,
    mac = mac,
    host = host,
    broadcastAddress = broadcastAddress,
    port = port,
    os = OsType.entries.firstOrNull { it.name == os } ?: OsType.LINUX,
    statusPort = statusPort,
    groupId = groupId,
)

fun Device.toEntity() = DeviceEntity(
    id = id,
    name = name,
    mac = mac,
    host = host,
    broadcastAddress = broadcastAddress,
    port = port,
    os = os.name,
    statusPort = statusPort,
    groupId = groupId,
)
