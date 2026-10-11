package com.diegobergonsi.wakedeck.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diegobergonsi.wakedeck.domain.Group

@Entity(tableName = "device_groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

fun GroupEntity.toDomain() = Group(id, name)
fun Group.toEntity() = GroupEntity(id, name)
