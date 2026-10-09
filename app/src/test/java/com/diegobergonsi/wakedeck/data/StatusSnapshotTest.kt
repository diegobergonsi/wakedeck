package com.diegobergonsi.wakedeck.data

import com.diegobergonsi.wakedeck.data.widget.StatusSnapshot
import com.diegobergonsi.wakedeck.domain.DeviceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class StatusSnapshotTest {
    private val minute = 60_000L

    @Test
    fun freshStatusIsKept() {
        val s = StatusSnapshot(DeviceStatus.ONLINE, checkedAtMs = 0)
        assertEquals(DeviceStatus.ONLINE, s.effective(10 * minute))
        assertEquals(DeviceStatus.ONLINE, s.effective(45 * minute))
    }

    @Test
    fun oldOnlineAndOfflineBecomeStale() {
        assertEquals(DeviceStatus.STALE, StatusSnapshot(DeviceStatus.ONLINE, 0).effective(45 * minute + 1))
        assertEquals(DeviceStatus.STALE, StatusSnapshot(DeviceStatus.OFFLINE, 0).effective(120 * minute))
    }

    @Test
    fun otherStatusesNeverGoStale() {
        assertEquals(DeviceStatus.UNAVAILABLE, StatusSnapshot(DeviceStatus.UNAVAILABLE, 0).effective(500 * minute))
        assertEquals(DeviceStatus.CHECKING, StatusSnapshot(DeviceStatus.CHECKING, 0).effective(500 * minute))
    }

    @Test
    fun valuesWithoutTimestampAreKept() {
        assertEquals(DeviceStatus.ONLINE, StatusSnapshot(DeviceStatus.ONLINE, null).effective(1_000 * minute))
    }
}
