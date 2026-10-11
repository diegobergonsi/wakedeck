package com.diegobergonsi.wakedeck.ssh

import com.diegobergonsi.wakedeck.domain.OsType
import org.junit.Assert.assertEquals
import org.junit.Test

class ShutdownCommandTest {
    @Test
    fun linuxUsesNonInteractiveSudo() {
        assertEquals("sudo -n shutdown -h now", ShutdownCommand.forOs(OsType.LINUX))
    }

    @Test
    fun windowsUsesShutdownExe() {
        assertEquals("shutdown /s /t 0", ShutdownCommand.forOs(OsType.WINDOWS))
    }
}
