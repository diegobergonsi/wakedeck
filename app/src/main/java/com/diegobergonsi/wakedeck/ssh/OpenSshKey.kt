package com.diegobergonsi.wakedeck.ssh

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Base64

/** Pure helpers for the OpenSSH text formats of an ECDSA P-256 key. No Android classes, so they are unit-tested. */
object OpenSshKey {
    const val ALGORITHM = "ecdsa-sha2-nistp256"
    private const val CURVE = "nistp256"

    /** Uncompressed EC point (0x04 || X || Y), each coordinate left-padded to 32 bytes. */
    fun uncompressedPoint(x: BigInteger, y: BigInteger): ByteArray {
        fun fixed(v: BigInteger): ByteArray {
            val raw = v.toByteArray().dropWhile { it == 0.toByte() }.toByteArray()
            require(raw.size <= 32) { "coordinate too large for P-256" }
            return ByteArray(32 - raw.size) + raw
        }
        return byteArrayOf(0x04) + fixed(x) + fixed(y)
    }

    /** The public key blob used both in authorized_keys and in the SHA256 fingerprint. */
    fun blob(point: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        listOf(ALGORITHM.toByteArray(), CURVE.toByteArray(), point).forEach { field ->
            out.write(field.size ushr 24); out.write(field.size ushr 16); out.write(field.size ushr 8); out.write(field.size)
            out.write(field)
        }
        return out.toByteArray()
    }

    /** One line to paste into authorized_keys. */
    fun authorizedKeysLine(point: ByteArray, comment: String): String =
        "$ALGORITHM ${Base64.getEncoder().encodeToString(blob(point))} $comment"

    /** Same format as `ssh-keygen -lf`: SHA256: plus unpadded base64 of the blob hash. */
    fun fingerprint(blob: ByteArray): String =
        "SHA256:" + Base64.getEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256").digest(blob))
}
