package com.diegobergonsi.wakedeck.ssh

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The app's one SSH key: ECDSA P-256 in the Android Keystore. The private key can sign but never leaves the
 * Keystore, and no password is involved. Only the public key is ever shown or copied.
 */
@Singleton
class SshKeyManager @Inject constructor() {
    private val keyStore: KeyStore get() = KeyStore.getInstance(PROVIDER).apply { load(null) }

    fun hasKey(): Boolean = keyStore.containsAlias(ALIAS)

    /** Creates the key pair if it does not exist yet. */
    fun ensureKey() {
        if (hasKey()) return
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, PROVIDER)
        generator.initialize(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build(),
        )
        generator.generateKeyPair()
    }

    /** Deletes the key. Every PC then has to be given the new public key. */
    fun deleteKey() {
        if (hasKey()) keyStore.deleteEntry(ALIAS)
    }

    fun privateKey(): PrivateKey {
        ensureKey()
        return keyStore.getKey(ALIAS, null) as PrivateKey
    }

    fun publicKey(): ECPublicKey {
        ensureKey()
        return keyStore.getCertificate(ALIAS).publicKey as ECPublicKey
    }

    private fun point(): ByteArray = publicKey().w.let { OpenSshKey.uncompressedPoint(it.affineX, it.affineY) }

    /** The line to put in the PC's authorized_keys file. */
    fun authorizedKeysLine(): String = OpenSshKey.authorizedKeysLine(point(), COMMENT)

    fun fingerprint(): String = OpenSshKey.fingerprint(OpenSshKey.blob(point()))

    private companion object {
        const val PROVIDER = "AndroidKeyStore"
        const val ALIAS = "wakedeck-ssh"
        const val COMMENT = "wakedeck"
    }
}
