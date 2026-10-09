package com.diegobergonsi.wakedeck.widget.config

/** Parses and formats `#RRGGBB` colors for the custom color field. Results are opaque ARGB ints. */
object ColorHex {
    fun parse(input: String): Int? {
        val hex = input.trim().removePrefix("#")
        if (hex.length != 6) return null
        val rgb = hex.toIntOrNull(16) ?: return null
        if (hex.any { Character.digit(it, 16) < 0 }) return null
        return 0xFF000000.toInt() or rgb
    }

    fun format(argb: Int): String = "#%06X".format(argb and 0xFFFFFF)
}
