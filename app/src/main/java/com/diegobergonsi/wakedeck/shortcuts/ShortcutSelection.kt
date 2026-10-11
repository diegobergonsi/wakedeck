package com.diegobergonsi.wakedeck.shortcuts

import com.diegobergonsi.wakedeck.domain.Device

object ShortcutSelection {
    const val MAX_SHORTCUTS = 4

    /** Devices the user marked for the launcher menu, by name, at most [MAX_SHORTCUTS]. */
    fun pick(devices: List<Device>): List<Device> =
        devices.filter { it.showInLauncher }.sortedBy { it.name.lowercase() }.take(MAX_SHORTCUTS)
}
