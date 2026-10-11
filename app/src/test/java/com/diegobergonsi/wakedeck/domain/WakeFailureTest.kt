package com.diegobergonsi.wakedeck.domain

import java.io.IOException
import java.net.SocketException
import java.net.UnknownHostException
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class WakeFailureTest {
    @Test fun unknownHostIsBadAddress() = assertEquals(WakeFailure.BAD_ADDRESS, WakeFailure.from(UnknownHostException("x")))

    @Test fun socketErrorIsNoNetwork() {
        assertEquals(WakeFailure.NO_NETWORK, WakeFailure.from(SocketException("Network is unreachable")))
        assertEquals(WakeFailure.NO_NETWORK, WakeFailure.from(IOException("x")))
    }

    @Test fun otherErrorIsOther() = assertEquals(WakeFailure.OTHER, WakeFailure.from(IllegalStateException("x")))

    @Test fun validBroadcastAddresses() {
        assertTrue(BroadcastAddress.isValid("192.168.1.255"))
        assertTrue(BroadcastAddress.isValid("255.255.255.255"))
        assertTrue(BroadcastAddress.isValid(" home.example.com "))
    }

    @Test fun invalidBroadcastAddresses() {
        assertFalse(BroadcastAddress.isValid(""))
        assertFalse(BroadcastAddress.isValid("192.168.1"))
        assertFalse(BroadcastAddress.isValid("192.168.1.256"))
        assertFalse(BroadcastAddress.isValid("1.2.3.4.5"))
        assertFalse(BroadcastAddress.isValid("my pc"))
    }
}
