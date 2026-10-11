package com.diegobergonsi.wakedeck.data.transfer

import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.Group
import com.diegobergonsi.wakedeck.domain.MagicPacket
import com.diegobergonsi.wakedeck.domain.OsType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** One device as stored in an export file. Groups are by name, so ids do not matter between phones. */
data class TransferDevice(
    val name: String,
    val mac: String,
    val host: String,
    val broadcastAddress: String,
    val port: Int,
    val os: OsType,
    val statusPort: Int?,
    val group: String?,
    val showInLauncher: Boolean,
    val sshUser: String,
    val sshPort: Int,
)

sealed interface DecodeResult {
    /** [invalid] counts entries that were skipped because they were unusable (no name, bad MAC, ...). */
    data class Success(val devices: List<TransferDevice>, val invalid: Int) : DecodeResult
    data class Failure(val reason: String) : DecodeResult
}

data class ImportPlan(val toAdd: List<TransferDevice>, val skippedExisting: Int, val invalid: Int)

/**
 * Export/import of the device list as JSON. The file holds no secrets: no SSH key (that never leaves the
 * Keystore) and no stored SSH host-key fingerprints (the new phone asks to trust each PC again).
 */
object DeviceTransfer {
    const val FORMAT_VERSION = 1
    private const val APP = "WakeDeck"

    fun encode(devices: List<Device>, groups: List<Group>): String {
        val groupNames = groups.associate { it.id to it.name }
        val array = JSONArray()
        devices.forEach { d ->
            array.put(
                JSONObject().apply {
                    put("name", d.name)
                    put("mac", d.mac)
                    put("host", d.host)
                    put("broadcast", d.broadcastAddress)
                    put("port", d.port)
                    put("os", d.os.name)
                    d.statusPort?.let { put("statusPort", it) }
                    d.groupId?.let { groupNames[it] }?.let { put("group", it) }
                    put("showInLauncher", d.showInLauncher)
                    put("sshUser", d.sshUser)
                    put("sshPort", d.sshPort)
                },
            )
        }
        return JSONObject().apply {
            put("app", APP)
            put("version", FORMAT_VERSION)
            put("devices", array)
        }.toString(2)
    }

    fun decode(text: String): DecodeResult {
        val root = try {
            JSONObject(text)
        } catch (e: JSONException) {
            return DecodeResult.Failure("not a WakeDeck file")
        }
        if (root.optString("app") != APP) return DecodeResult.Failure("not a WakeDeck file")
        if (root.optInt("version", 0) > FORMAT_VERSION) return DecodeResult.Failure("file is from a newer WakeDeck")
        val array = root.optJSONArray("devices") ?: return DecodeResult.Failure("no devices")

        val devices = mutableListOf<TransferDevice>()
        var invalid = 0
        for (i in 0 until array.length()) {
            val o = array.optJSONObject(i)
            val device = o?.let { parseDevice(it) }
            if (device == null) invalid++ else devices += device
        }
        return DecodeResult.Success(devices, invalid)
    }

    private fun parseDevice(o: JSONObject): TransferDevice? {
        val name = o.optString("name").trim()
        val mac = MagicPacket.parseMac(o.optString("mac"))?.let { MagicPacket.formatMac(it) } ?: return null
        if (name.isEmpty()) return null
        val port = o.optInt("port", Device.DEFAULT_PORT).takeIf { it in 1..65535 } ?: return null
        val sshPort = o.optInt("sshPort", Device.DEFAULT_SSH_PORT).takeIf { it in 1..65535 } ?: return null
        val statusPort = if (o.has("statusPort")) o.optInt("statusPort").takeIf { it in 1..65535 } ?: return null else null
        return TransferDevice(
            name = name,
            mac = mac,
            host = o.optString("host").trim(),
            broadcastAddress = o.optString("broadcast", Device.DEFAULT_BROADCAST).trim().ifBlank { Device.DEFAULT_BROADCAST },
            port = port,
            os = OsType.entries.firstOrNull { it.name == o.optString("os") } ?: OsType.LINUX,
            statusPort = statusPort,
            group = o.optString("group").trim().ifEmpty { null },
            showInLauncher = o.optBoolean("showInLauncher", false),
            sshUser = o.optString("sshUser").trim(),
            sshPort = sshPort,
        )
    }

    /** Skip-existing import: a device whose MAC is already saved (or repeats inside the file) is not added. */
    fun plan(existing: List<Device>, incoming: List<TransferDevice>, invalid: Int): ImportPlan {
        val seen = existing.mapNotNull { MagicPacket.parseMac(it.mac)?.let(MagicPacket::formatMac) }.toMutableSet()
        val toAdd = mutableListOf<TransferDevice>()
        var skipped = 0
        incoming.forEach { d -> if (seen.add(d.mac)) toAdd += d else skipped++ }
        return ImportPlan(toAdd, skipped, invalid)
    }
}
