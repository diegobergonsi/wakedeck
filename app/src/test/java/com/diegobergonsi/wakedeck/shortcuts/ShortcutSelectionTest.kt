package com.diegobergonsi.wakedeck.shortcuts

import com.diegobergonsi.wakedeck.domain.Device
import org.junit.Assert.assertEquals
import org.junit.Test

class ShortcutSelectionTest {
    private fun device(id: Long, name: String, show: Boolean) =
        Device(id = id, name = name, mac = "00:00:00:00:00:00", showInLauncher = show)

    @Test
    fun onlyMarkedDevicesAreChosen() {
        val picked = ShortcutSelection.pick(listOf(device(1, "A", true), device(2, "B", false), device(3, "C", true)))
        assertEquals(listOf(1L, 3L), picked.map { it.id })
    }

    @Test
    fun orderedByNameIgnoringCase() {
        val picked = ShortcutSelection.pick(listOf(device(1, "zeta", true), device(2, "Alpha", true), device(3, "beta", true)))
        assertEquals(listOf("Alpha", "beta", "zeta"), picked.map { it.name })
    }

    @Test
    fun cappedAtFour() {
        val many = (1L..7L).map { device(it, "D$it", true) }
        assertEquals(ShortcutSelection.MAX_SHORTCUTS, ShortcutSelection.pick(many).size)
    }

    @Test
    fun noneMarkedGivesNoShortcuts() {
        assertEquals(emptyList<Device>(), ShortcutSelection.pick(listOf(device(1, "A", false))))
    }
}
