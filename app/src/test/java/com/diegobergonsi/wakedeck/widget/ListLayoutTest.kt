package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.DensityOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ListLayoutTest {
    @Test
    fun rowsShareTheHeight() {
        assertEquals(60f, ListLayout.rowHeightDp(120f, 2, DensityOption.COMFORTABLE), 0.01f)
    }

    @Test
    fun rowsAreCappedSoFewDevicesDoNotGetHuge() {
        assertEquals(76f, ListLayout.rowHeightDp(400f, 2, DensityOption.COMFORTABLE), 0.01f)
        assertEquals(56f, ListLayout.rowHeightDp(400f, 2, DensityOption.COMPACT), 0.01f)
    }

    @Test
    fun manyDevicesKeepAMinimumRowHeightAndScroll() {
        assertEquals(48f, ListLayout.rowHeightDp(100f, 6, DensityOption.COMFORTABLE), 0.01f)
        assertEquals(48f, ListLayout.rowHeightDp(100f, 6, DensityOption.COMPACT), 0.01f)
    }
}
