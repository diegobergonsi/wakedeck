package com.diegobergonsi.wakedeck.data.transfer

import com.diegobergonsi.wakedeck.domain.BroadcastAddress
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
    data object TooMany : DecodeResult
}

data class ImportPlan(val toAdd: List<TransferDevice>, val skippedExisting: Int, val invalid: Int)

/**
 * Export/import of the device list as JSON. The file holds no secrets: no SSH key (that never leaves the
 * Keystore) and no stored SSH host-key fingerprints (the new phone asks to trust each PC again).
 */
object DeviceTransfer {
    const val FORMAT_VERSION = 1

    /** Most devices the app holds, and most a file may carry. Every device is checked on the network every few seconds. */
    const val MAX_DEVICES = 200
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
        if (array.length() > MAX_DEVICES) return DecodeResult.TooMany

        val devices = mutableListOf<TransferDevice>()
        var invalid = 0
        for (i in 0 until array.length()) {
            val o = array.optJSONObject(i)
            val device = o?.let { parseDevice(it) }
            if (device == null) invalid++ else devices += device
        }
        return DecodeResult.Success(devices, invalid)
    }

    /** Empty for a missing key or a JSON null; plain optString would turn a JSON null into the text "null". */
    private fun JSONObject.str(key: String): String = if (isNull(key)) "" else optString(key)

    private fun parseDevice(o: JSONObject): TransferDevice? {
        val name = o.str("name").trim()
        val mac = MagicPacket.parseMac(o.str("mac"))?.let { MagicPacket.formatMac(it) } ?: return null
        if (name.isEmpty() || name.length > Device.MAX_NAME) return null
        val port = o.optInt("port", Device.DEFAULT_PORT).takeIf { it in 1..65535 } ?: return null
        val sshPort = o.optInt("sshPort", Device.DEFAULT_SSH_PORT).takeIf { it in 1..65535 } ?: return null
        val broadcast = o.str("broadcast").trim().ifBlank { Device.DEFAULT_BROADCAST }
        if (!BroadcastAddress.isValid(broadcast)) return null
        val sshUser = o.str("sshUser").trim()
        if (sshUser.any { it.isWhitespace() } || sshUser.length > Device.MAX_SSH_USER) return null
        val host = o.str("host").trim()
        val group = o.str("group").trim().ifEmpty { null }
        if (host.length > Device.MAX_HOST || (group?.length ?: 0) > Device.MAX_NAME) return null
        val statusPort = if (o.has("statusPort")) o.optInt("statusPort").takeIf { it in 1..65535 } ?: return null else null
        return TransferDevice(
            name = name,
            mac = mac,
            host = host,
            broadcastAddress = broadcast,
            port = port,
            os = OsType.entries.firstOrNull { it.name == o.str("os") } ?: OsType.LINUX,
            statusPort = statusPort,
            group = group,
            showInLauncher = o.optBoolean("showInLauncher", false),
            sshUser = sshUser,
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
