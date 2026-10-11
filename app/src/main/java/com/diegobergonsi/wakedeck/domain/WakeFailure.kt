package com.diegobergonsi.wakedeck.domain

import java.net.UnknownHostException

/** Why a magic packet could not be sent. */
enum class WakeFailure {
    /** The broadcast address could not be resolved. */
    BAD_ADDRESS,

    /** The phone has no route to that address (no Wi-Fi, no VPN, airplane mode). */
    NO_NETWORK,
    OTHER,
    ;

    companion object {
        fun from(error: Throwable): WakeFailure = when {
            error is UnknownHostException -> BAD_ADDRESS
            error is java.io.IOException -> NO_NETWORK
            else -> OTHER
        }
    }
}

/** Accepts an IPv4 address or a host name. Text made only of digits and dots must be a full IPv4 address. */
object BroadcastAddress {
    fun isValid(text: String): Boolean {
        val value = text.trim()
        if (value.isEmpty() || value.any { it.isWhitespace() }) return false
        if (value.all { it.isDigit() || it == '.' }) {
            val parts = value.split('.')
            return parts.size == 4 && parts.all { p -> p.isNotEmpty() && p.length <= 3 && p.toInt() in 0..255 }
        }
        return true
    }
}
