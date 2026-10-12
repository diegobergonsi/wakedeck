package com.diegobergonsi.wakedeck.domain

enum class OsType { WINDOWS, LINUX, MACOS }

data class Device(
    val id: Long = 0,
    val name: String,
    /** Canonical form: AA:BB:CC:DD:EE:FF */
    val mac: String,
    /** IP or hostname of the machine itself. Used for status checks. May be empty. */
    val host: String = "",
    /** Target of the magic packet. Editable so remote wake over VPN can use a subnet broadcast. */
    val broadcastAddress: String = DEFAULT_BROADCAST,
    val port: Int = DEFAULT_PORT,
    val os: OsType = OsType.LINUX,
    val statusPort: Int? = null,
    val groupId: Long? = null,
    /** Offer a wake shortcut when long-pressing the launcher icon (the launcher shows about 4). */
    val showInLauncher: Boolean = false,
    /** Remote shutdown over SSH (key authentication only). Empty user = not set up. */
    val sshUser: String = "",
    val sshPort: Int = DEFAULT_SSH_PORT,
    /** SHA256 fingerprint of the PC's SSH host key, confirmed by the user on first use. Empty = not confirmed. */
    val sshHostKey: String = "",
) {
    companion object {
        const val DEFAULT_BROADCAST = "255.255.255.255"
        const val DEFAULT_PORT = 9
        const val DEFAULT_SSH_PORT = 22
        const val MAX_NAME = 100
        const val MAX_HOST = 255
        const val MAX_SSH_USER = 64
    }
}
