package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.ColorHex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ColorHexTest {
    @Test
    fun parsesWithAndWithoutHash() {
        assertEquals(0xFF112233.toInt(), ColorHex.parse("#112233"))
        assertEquals(0xFFAABBCC.toInt(), ColorHex.parse("aabbcc"))
        assertEquals(0xFF00FF00.toInt(), ColorHex.parse("  #00ff00  "))
    }

    @Test
    fun rejectsIncompleteOrInvalidInput() {
        for (s in listOf("", "#", "#12345", "#1234567", "#GG0000", "#12 456", "red", "#+12345")) assertNull(s, ColorHex.parse(s))
    }

    @Test
    fun formatsUppercaseAndIgnoresAlpha() {
        assertEquals("#112233", ColorHex.format(0xFF112233.toInt()))
        assertEquals("#ABCDEF", ColorHex.format(0x80ABCDEF.toInt()))
        assertEquals("#000000", ColorHex.format(0xFF000000.toInt()))
    }

    @Test
    fun roundTrip() {
        val c = ColorHex.parse("#3A7BD5")!!
        assertEquals("#3A7BD5", ColorHex.format(c))
    }
}
