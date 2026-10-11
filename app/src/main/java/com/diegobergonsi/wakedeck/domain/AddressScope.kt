package com.diegobergonsi.wakedeck.domain

/** Tells whether an address stays inside the user's own network. Used to warn before importing a file that points elsewhere. */
object AddressScope {
    private val LOCAL_SUFFIXES = listOf(".local", ".lan", ".home", ".internal", ".home.arpa")

    /** Empty counts as local: there is nothing to contact. Private, link-local, loopback, CGNAT (VPN) and broadcast ranges count as local. */
    fun isLocal(address: String): Boolean {
        val value = address.trim().lowercase()
        if (value.isEmpty()) return true
        if (':' in value) return isLocalIpv6(value)
        if (value.all { it.isDigit() || it == '.' }) return isLocalIpv4(value)
        return '.' !in value || LOCAL_SUFFIXES.any { value.endsWith(it) }
    }

    private fun isLocalIpv4(value: String): Boolean {
        val p = value.split('.').map { it.toIntOrNull() ?: return false }
        if (p.size != 4) return false
        val (a, b) = p
        return a == 10 || a == 127 || (a == 172 && b in 16..31) || (a == 192 && b == 168) ||
            (a == 169 && b == 254) || (a == 100 && b in 64..127) || p.all { it == 255 }
    }

    private fun isLocalIpv6(value: String): Boolean =
        value == "::1" || value.startsWith("fe80:") || value.startsWith("fc") || value.startsWith("fd")
}
