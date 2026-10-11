package com.diegobergonsi.wakedeck.ssh

/** Trust on first use: remember a PC's SSH host key fingerprint, refuse if it ever changes. */
object HostKeyPolicy {
    enum class Decision { UNKNOWN, MATCH, MISMATCH }

    fun decide(stored: String, seen: String): Decision = when {
        stored.isBlank() -> Decision.UNKNOWN
        stored == seen -> Decision.MATCH
        else -> Decision.MISMATCH
    }
}
