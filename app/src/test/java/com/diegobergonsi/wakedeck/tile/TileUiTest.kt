package com.diegobergonsi.wakedeck.tile

import com.diegobergonsi.wakedeck.domain.DeviceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TileUiTest {
    @Test
    fun onlyOnlineLightsTheTile() {
        assertTrue(TileUi.isActive(DeviceStatus.ONLINE))
        for (s in listOf(DeviceStatus.OFFLINE, DeviceStatus.CHECKING, DeviceStatus.UNAVAILABLE, DeviceStatus.STALE)) {
            assertFalse(s.name, TileUi.isActive(s))
        }
    }

    @Test
    fun slotIsReadFromTheServiceClassName() {
        assertEquals(1, TileUi.slotFor("com.diegobergonsi.wakedeck.tile.WakeTile1Service"))
        assertEquals(3, TileUi.slotFor("com.diegobergonsi.wakedeck.tile.WakeTile3Service"))
    }

    @Test
    fun unknownClassHasNoSlot() {
        assertNull(TileUi.slotFor("com.example.Other"))
        assertNull(TileUi.slotFor("com.diegobergonsi.wakedeck.tile.WakeTile4Service"))
        assertNull(TileUi.slotFor(null))
    }

    @Test
    fun statusGoesInTheLabelWhereSubtitlesAreHidden() {
        assertEquals("PC\nOnline" to null, TileUi.texts("PC", "Online", "HONOR"))
        assertEquals("PC\nOnline" to null, TileUi.texts("PC", "Online", "HUAWEI"))
    }

    @Test
    fun otherPanelsKeepTheSubtitle() {
        assertEquals("PC" to "Online", TileUi.texts("PC", "Online", "Google"))
        assertEquals("PC" to "Online", TileUi.texts("PC", "Online", null))
    }

    @Test
    fun noStatusMeansLabelOnly() {
        assertEquals("Choose device" to null, TileUi.texts("Choose device", null, "HONOR"))
    }
}
