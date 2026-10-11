package com.diegobergonsi.wakedeck.data

import com.diegobergonsi.wakedeck.data.db.GroupDao
import com.diegobergonsi.wakedeck.data.db.toDomain
import com.diegobergonsi.wakedeck.data.db.toEntity
import com.diegobergonsi.wakedeck.domain.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(private val dao: GroupDao) {
    fun observeAll(): Flow<List<Group>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getAll(): List<Group> = dao.getAll().map { it.toDomain() }

    /** Returns the id, also for a new group. */
    suspend fun save(group: Group): Long = dao.upsert(group.toEntity()).let { if (group.id == 0L) it else group.id }

    suspend fun delete(id: Long) = dao.delete(id)
}
