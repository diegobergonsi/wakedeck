package com.diegobergonsi.wakedeck.tile

import com.diegobergonsi.wakedeck.domain.DeviceStatus

object TileUi {
    const val SLOTS = 3

    /** A tile lights up only while its device is online. */
    fun isActive(status: DeviceStatus): Boolean = status == DeviceStatus.ONLINE

    /** Slot number (1..3) for a tile service class name, or null if it is not one of ours. */
    fun slotFor(className: String?): Int? =
        (1..SLOTS).firstOrNull { className?.endsWith("WakeTile${it}Service") == true }

    /** Honor and Huawei panels draw only the tile label, never the subtitle line. */
    private val labelOnlyManufacturers = setOf("honor", "huawei")

    fun showsSubtitle(manufacturer: String?): Boolean = manufacturer?.lowercase() !in labelOnlyManufacturers

    /** Label and subtitle for a tile. Where the panel hides subtitles, the status moves into the label. */
    fun texts(name: String, status: String?, manufacturer: String?): Pair<String, String?> = when {
        status == null -> name to null
        showsSubtitle(manufacturer) -> name to status
        else -> "$name\n$status" to null
    }
}
