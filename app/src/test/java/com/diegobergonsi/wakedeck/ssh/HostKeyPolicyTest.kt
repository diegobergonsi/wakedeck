package com.diegobergonsi.wakedeck.ssh

import com.diegobergonsi.wakedeck.ssh.HostKeyPolicy.Decision
import org.junit.Assert.assertEquals
import org.junit.Test

class HostKeyPolicyTest {
    @Test
    fun noStoredKeyMeansFirstUse() {
        assertEquals(Decision.UNKNOWN, HostKeyPolicy.decide("", "SHA256:abc"))
        assertEquals(Decision.UNKNOWN, HostKeyPolicy.decide("   ", "SHA256:abc"))
    }

    @Test
    fun sameKeyMatches() {
        assertEquals(Decision.MATCH, HostKeyPolicy.decide("SHA256:abc", "SHA256:abc"))
    }

    @Test
    fun differentKeyIsRefused() {
        assertEquals(Decision.MISMATCH, HostKeyPolicy.decide("SHA256:abc", "SHA256:xyz"))
    }
}
