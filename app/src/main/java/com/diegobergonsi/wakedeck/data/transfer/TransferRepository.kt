package com.diegobergonsi.wakedeck.data.transfer

import com.diegobergonsi.wakedeck.data.db.AppDatabase
import androidx.room.withTransaction
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.GroupRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.Group
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ImportOutcome {
    data class Done(val added: Int, val skipped: Int, val invalid: Int) : ImportOutcome
    data class Failed(val reason: String) : ImportOutcome
}

@Singleton
class TransferRepository @Inject constructor(
    private val devices: DeviceRepository,
    private val groups: GroupRepository,
    private val db: AppDatabase,
) {
    /** The export file text, and how many devices are in it. */
    suspend fun export(): Pair<String, Int> {
        val all = devices.observeAll().first()
        return DeviceTransfer.encode(all, groups.getAll()) to all.size
    }

    suspend fun import(text: String): ImportOutcome {
        val decoded = when (val d = DeviceTransfer.decode(text)) {
            is DecodeResult.Failure -> return ImportOutcome.Failed(d.reason)
            is DecodeResult.Success -> d
        }
        val plan = DeviceTransfer.plan(devices.observeAll().first(), decoded.devices, decoded.invalid)

        // One transaction: a failure part-way leaves nothing half-imported.
        db.withTransaction {
            // Groups are matched by name, ignoring case; missing ones are created.
            val byName = groups.getAll().associateBy { it.name.lowercase() }.toMutableMap()
            plan.toAdd.mapNotNull { it.group }.distinctBy { it.lowercase() }.forEach { name ->
                if (name.lowercase() !in byName) byName[name.lowercase()] = Group(groups.save(Group(name = name)), name)
            }
            plan.toAdd.forEach { t ->
                devices.save(
                    Device(
                        name = t.name,
                        mac = t.mac,
                        host = t.host,
                        broadcastAddress = t.broadcastAddress,
                        port = t.port,
                        os = t.os,
                        statusPort = t.statusPort,
                        groupId = t.group?.let { byName[it.lowercase()]?.id },
                        showInLauncher = t.showInLauncher,
                        sshUser = t.sshUser,
                        sshPort = t.sshPort,
                    ),
                )
            }
        }
        return ImportOutcome.Done(plan.toAdd.size, plan.skippedExisting, plan.invalid)
    }
}
