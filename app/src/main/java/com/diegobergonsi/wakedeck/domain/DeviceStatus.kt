package com.diegobergonsi.wakedeck.domain

enum class DeviceStatus {
    /** Check not finished yet. */
    CHECKING,
    ONLINE,
    OFFLINE,
    /** No host set, so nothing to check. */
    UNAVAILABLE,
    /** Last known status is too old to trust (widgets only). */
    STALE,
}
