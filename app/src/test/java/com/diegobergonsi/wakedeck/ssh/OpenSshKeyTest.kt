package com.diegobergonsi.wakedeck.ssh

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigInteger

/** Expected values come from `ssh-keygen -t ecdsa -b 256` and `ssh-keygen -lf` on the same key. */
class OpenSshKeyTest {
    private val pointHex =
        "0405a7e5edaa2948a06fdd25c996ffac9d6890c03af6f88c3a22abdd72edca6b04729f6045521e12f65ecd7551f60240569e00dbd46210d165663b86c9d49a0eb2"
    private val point = pointHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    @Test
    fun authorizedKeysLineMatchesSshKeygen() {
        assertEquals(
            "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBAWn5e2qKUigb90lyZb/rJ1okMA69viMOiKr3XLtymsEcp9gRVIeEvZezXVR9gJAVp4A29RiENFlZjuGydSaDrI= wakedeck-test",
            OpenSshKey.authorizedKeysLine(point, "wakedeck-test"),
        )
    }

    @Test
    fun fingerprintMatchesSshKeygen() {
        assertEquals(
            "SHA256:s06yH032rSKFNA1jFn0Ap/hUd5oPZiYqnWa0j7oxEJg",
            OpenSshKey.fingerprint(OpenSshKey.blob(point)),
        )
    }

    @Test
    fun pointIsBuiltFromCoordinates() {
        val x = BigInteger(1, point.copyOfRange(1, 33))
        val y = BigInteger(1, point.copyOfRange(33, 65))
        assertArrayEquals(point, OpenSshKey.uncompressedPoint(x, y))
    }

    @Test
    fun shortCoordinatesArePaddedToThirtyTwoBytes() {
        val p = OpenSshKey.uncompressedPoint(BigInteger.ONE, BigInteger.TWO)
        assertEquals(65, p.size)
        assertEquals(0x04.toByte(), p[0])
        assertEquals(1.toByte(), p[32])
        assertEquals(2.toByte(), p[64])
    }

    @Test
    fun oversizedCoordinateIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            OpenSshKey.uncompressedPoint(BigInteger.ONE.shiftLeft(256), BigInteger.ONE)
        }
    }
}
