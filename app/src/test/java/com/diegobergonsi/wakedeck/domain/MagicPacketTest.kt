package com.diegobergonsi.wakedeck.domain

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class MagicPacketTest {
    private val mac = byteArrayOf(0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E)

    @Test
    fun packetIs102Bytes() {
        assertEquals(102, MagicPacket.build(mac).size)
        assertEquals(102, MagicPacket.SIZE)
    }

    @Test
    fun packetStartsWithSixFF() {
        val p = MagicPacket.build(mac)
        for (i in 0 until 6) assertEquals(0xFF.toByte(), p[i])
    }

    @Test
    fun macRepeated16Times() {
        val p = MagicPacket.build(mac)
        for (rep in 0 until 16) {
            assertArrayEquals(mac, p.copyOfRange(6 + rep * 6, 12 + rep * 6))
        }
    }

    @Test
    fun highBytesAreNotSignExtended() {
        val high = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte())
        assertArrayEquals(high, MagicPacket.parseMac("AA:BB:CC:DD:EE:FF"))
    }

    @Test
    fun parsesAllSeparatorStyles() {
        val inputs = listOf(
            "00:1a:2b:3c:4d:5e",
            "00:1A:2B:3C:4D:5E",
            "00-1A-2B-3C-4D-5E",
            "001a.2b3c.4d5e",
            "001A2B3C4D5E",
            "  00:1a:2b:3c:4d:5e  ",
        )
        for (s in inputs) assertArrayEquals(s, mac, MagicPacket.parseMac(s))
    }

    @Test
    fun rejectsInvalidMacs() {
        val bad = listOf("", "   ", "00:1a:2b:3c:4d", "00:1a:2b:3c:4d:5e:6f", "GG:1a:2b:3c:4d:5e", "00:1a:2b:3c:4d:5", "hello", "00:1a:2b:3c:4d:5é")
        for (s in bad) assertNull(s, MagicPacket.parseMac(s))
    }

    @Test
    fun buildRejectsWrongLength() {
        assertThrows(IllegalArgumentException::class.java) { MagicPacket.build(ByteArray(5)) }
        assertThrows(IllegalArgumentException::class.java) { MagicPacket.build(ByteArray(7)) }
    }

    @Test
    fun formatMacIsUppercaseColon() {
        assertEquals("00:1A:2B:3C:4D:5E", MagicPacket.formatMac(mac))
        assertEquals("AA:BB:CC:DD:EE:FF", MagicPacket.formatMac(MagicPacket.parseMac("aabbccddeeff")!!))
    }

    @Test
    fun parseThenBuildRoundTrip() {
        val parsed = MagicPacket.parseMac("00-1A-2B-3C-4D-5E")
        assertNotNull(parsed)
        assertArrayEquals(MagicPacket.build(mac), MagicPacket.build(parsed!!))
    }
}
