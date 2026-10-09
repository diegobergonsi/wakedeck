package com.diegobergonsi.wakedeck.widget

import org.junit.Assert.assertEquals
import org.junit.Test

class GridLayoutTest {
    @Test
    fun autoPicksColumnsFromWidth() {
        assertEquals(1, GridLayout.columnsFor(80f, 0))
        assertEquals(2, GridLayout.columnsFor(180f, 0))
        assertEquals(3, GridLayout.columnsFor(260f, 0))
        assertEquals(4, GridLayout.columnsFor(900f, 0))
    }

    @Test
    fun requestedColumnsAreKeptWhenTheyFit() {
        assertEquals(3, GridLayout.columnsFor(300f, 3))
        assertEquals(1, GridLayout.columnsFor(300f, 1))
    }

    @Test
    fun requestedColumnsAreReducedWhenTooNarrow() {
        assertEquals(2, GridLayout.columnsFor(130f, 4))
        assertEquals(1, GridLayout.columnsFor(40f, 4))
    }

    @Test
    fun neverBelowOneOrAboveFour() {
        assertEquals(1, GridLayout.columnsFor(0f, 0))
        assertEquals(4, GridLayout.columnsFor(2000f, 9))
    }

    @Test
    fun autoColumnsPicksARowForWideShortSlots() {
        assertEquals(2, GridLayout.autoColumns(190f, 76f, 2))
    }

    @Test
    fun autoColumnsStacksFewDevicesInTallSlots() {
        assertEquals(1, GridLayout.autoColumns(120f, 260f, 2))
    }

    @Test
    fun autoColumnsNeverExceedsDeviceCountAndSingleDeviceIsOneColumn() {
        assertEquals(1, GridLayout.autoColumns(400f, 100f, 1))
        assertEquals(2, GridLayout.autoColumns(400f, 70f, 2))
    }

    @Test
    fun tileHeightSharesHeightWithinLimits() {
        assertEquals(66f, GridLayout.tileHeightDp(120f, 2), 0.01f)
        assertEquals(100f, GridLayout.tileHeightDp(200f, 2), 0.01f)
        assertEquals(120f, GridLayout.tileHeightDp(400f, 1), 0.01f)
        assertEquals(66f, GridLayout.tileHeightDp(60f, 3), 0.01f)
    }
}
