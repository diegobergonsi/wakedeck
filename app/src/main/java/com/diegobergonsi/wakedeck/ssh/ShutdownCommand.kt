package com.diegobergonsi.wakedeck.ssh

import com.diegobergonsi.wakedeck.domain.OsType

object ShutdownCommand {
    /**
     * Linux needs a passwordless sudo rule for shutdown (`sudo -n` fails instead of waiting for a password).
     * macOS is the same, with Remote Login turned on.
     * Windows needs OpenSSH Server running; the account must be allowed to shut down.
     */
    fun forOs(os: OsType): String = when (os) {
        OsType.LINUX, OsType.MACOS -> "sudo -n shutdown -h now"
        OsType.WINDOWS -> "shutdown /s /t 0"
    }
}
