package com.diegobergonsi.wakedeck.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.ServerSocket

class StatusCheckerTest {
    private val checker = StatusChecker()

    private fun device(host: String, statusPort: Int? = null) =
        Device(name = "t", mac = "00:11:22:33:44:55", host = host, statusPort = statusPort)

    @Test
    fun blankHostIsUnavailable() = runBlocking {
        assertEquals(DeviceStatus.UNAVAILABLE, checker.check(device("")))
        assertEquals(DeviceStatus.UNAVAILABLE, checker.check(device("   ", 22)))
    }

    @Test
    fun openPortIsOnline() = runBlocking {
        ServerSocket(0).use { server ->
            assertEquals(DeviceStatus.ONLINE, checker.check(device("127.0.0.1", server.localPort)))
        }
    }

    @Test
    fun refusedPortIsOnlineBecauseHostAnswered() = runBlocking {
        val closedPort = ServerSocket(0).use { it.localPort }
        assertEquals(DeviceStatus.ONLINE, checker.check(device("127.0.0.1", closedPort)))
    }

    @Test
    fun unresolvableHostIsOffline() = runBlocking {
        assertEquals(DeviceStatus.OFFLINE, checker.check(device("no-such-host.invalid", 22), timeoutMs = 500))
        assertEquals(DeviceStatus.OFFLINE, checker.check(device("no-such-host.invalid"), timeoutMs = 500))
    }

    @Test
    fun loopbackPingIsOnline() = runBlocking {
        assertEquals(DeviceStatus.ONLINE, checker.check(device("127.0.0.1")))
    }
}
