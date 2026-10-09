package com.diegobergonsi.wakedeck.domain

object MagicPacket {
    const val MAC_BYTES = 6
    const val SIZE = 6 + 16 * MAC_BYTES

    /**
     * Parses a MAC with `:`, `-` or `.` separators (or none). Returns null unless exactly 12 hex digits remain.
     */
    fun parseMac(input: String): ByteArray? {
        val hex = input.trim().filterNot { it == ':' || it == '-' || it == '.' }
        if (hex.length != MAC_BYTES * 2) return null
        val out = ByteArray(MAC_BYTES)
        for (i in 0 until MAC_BYTES) {
            val hi = Character.digit(hex[i * 2], 16)
            val lo = Character.digit(hex[i * 2 + 1], 16)
            if (hi < 0 || lo < 0) return null
            out[i] = ((hi shl 4) or lo).toByte()
        }
        return out
    }

    fun formatMac(mac: ByteArray): String =
        mac.joinToString(":") { "%02X".format(it.toInt() and 0xFF) }

    /** 6 x 0xFF followed by the MAC repeated 16 times. */
    fun build(mac: ByteArray): ByteArray {
        require(mac.size == MAC_BYTES) { "MAC must be $MAC_BYTES bytes" }
        val packet = ByteArray(SIZE)
        for (i in 0 until 6) packet[i] = 0xFF.toByte()
        for (rep in 0 until 16) mac.copyInto(packet, 6 + rep * MAC_BYTES)
        return packet
    }
}
