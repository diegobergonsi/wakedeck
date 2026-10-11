package com.diegobergonsi.wakedeck.ssh

import com.diegobergonsi.wakedeck.domain.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.hierynomus.sshj.key.KeyAlgorithms
import net.schmizz.sshj.DefaultConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.Buffer
import net.schmizz.sshj.common.KeyType
import net.schmizz.sshj.common.SecurityUtils
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.transport.verification.HostKeyVerifier
import net.schmizz.sshj.userauth.UserAuthException
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import java.io.IOException
import java.security.PrivateKey
import java.security.PublicKey
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed interface FingerprintResult {
    data class Found(val fingerprint: String) : FingerprintResult
    data class Failed(val reason: String) : FingerprintResult
}

sealed interface ShutdownResult {
    /** The command was sent. A dropped connection right after is normal: the PC is going down. */
    data object Sent : ShutdownResult
    data object NoHostKey : ShutdownResult
    data class HostKeyChanged(val expected: String, val seen: String) : ShutdownResult
    data object AuthFailed : ShutdownResult
    data object NotConfigured : ShutdownResult
    data class CommandFailed(val exitStatus: Int, val message: String) : ShutdownResult
    data class Unreachable(val reason: String) : ShutdownResult
}

/** Remote shutdown over SSH. Key authentication only; the host key must have been confirmed first. */
@Singleton
class SshShutdown @Inject constructor(private val keys: SshKeyManager) {
    init {
        // Use the platform crypto only. Keystore keys sign through it, BouncyCastle cannot use them.
        SecurityUtils.setRegisterBouncyCastle(false)
    }

    /** First connection: reads the PC's host key so the user can confirm its fingerprint. Does not log in. */
    suspend fun fetchHostKeyFingerprint(device: Device): FingerprintResult = withContext(Dispatchers.IO) {
        var seen: String? = null
        val client = newClient(object : HostKeyVerifier {
            override fun verify(hostname: String, port: Int, key: PublicKey): Boolean {
                seen = fingerprintOf(key)
                return true
            }

            override fun findExistingAlgorithms(hostname: String, port: Int): List<String> = emptyList()
        })
        try {
            client.connect(device.host.trim(), device.sshPort)
        } catch (e: IOException) {
            if (seen == null) return@withContext FingerprintResult.Failed(e.message ?: e.javaClass.simpleName)
        } finally {
            runCatching { client.disconnect() }
        }
        seen?.let { FingerprintResult.Found(it) } ?: FingerprintResult.Failed("no host key")
    }

    suspend fun shutdown(device: Device): ShutdownResult = withContext(Dispatchers.IO) {
        if (device.host.isBlank() || device.sshUser.isBlank()) return@withContext ShutdownResult.NotConfigured
        if (device.sshHostKey.isBlank()) return@withContext ShutdownResult.NoHostKey

        val mismatch = java.util.concurrent.atomic.AtomicReference<ShutdownResult.HostKeyChanged?>(null)
        val client = newClient(object : HostKeyVerifier {
            override fun verify(hostname: String, port: Int, key: PublicKey): Boolean {
                val seen = fingerprintOf(key)
                return when (HostKeyPolicy.decide(device.sshHostKey, seen)) {
                    HostKeyPolicy.Decision.MATCH -> true
                    else -> {
                        mismatch.set(ShutdownResult.HostKeyChanged(device.sshHostKey, seen))
                        false
                    }
                }
            }

            override fun findExistingAlgorithms(hostname: String, port: Int): List<String> = emptyList()
        })
        try {
            client.connect(device.host.trim(), device.sshPort)
            client.auth(device.sshUser.trim(), net.schmizz.sshj.userauth.method.AuthPublickey(keyProvider()))
            client.startSession().use { session -> run(session, ShutdownCommand.forOs(device.os)) }
        } catch (e: UserAuthException) {
            ShutdownResult.AuthFailed
        } catch (e: IOException) {
            mismatch.get() ?: ShutdownResult.Unreachable(e.message ?: e.javaClass.simpleName)
        } finally {
            runCatching { client.disconnect() }
        }
    }

    private fun run(session: Session, command: String): ShutdownResult {
        val cmd = session.exec(command)
        return try {
            cmd.join(COMMAND_WAIT_SECONDS, TimeUnit.SECONDS)
            val status = cmd.exitStatus
            when {
                status == null || status == 0 -> ShutdownResult.Sent
                else -> ShutdownResult.CommandFailed(status, readError(cmd))
            }
        } catch (e: IOException) {
            // The server closed the connection while shutting down: the command went through.
            ShutdownResult.Sent
        }
    }

    private fun readError(cmd: Session.Command): String =
        runCatching {
            // A hostile server could stream without end, so read one bounded chunk only.
            val buffer = ByteArray(MAX_ERROR_BYTES)
            val n = cmd.errorStream.read(buffer).coerceAtLeast(0)
            buffer.decodeToString(0, n).trim()
        }.getOrDefault("")

    /**
     * Only ECDSA and RSA host keys: with the platform crypto sshj cannot decode an Ed25519 host key. Every OpenSSH
     * server, Linux and Windows, also has ECDSA and RSA host keys, so the PC still has something to offer.
     */
    private fun newClient(verifier: HostKeyVerifier): SSHClient = SSHClient(
        DefaultConfig().apply {
            keyAlgorithms = listOf(
                KeyAlgorithms.ECDSASHANistp256(),
                KeyAlgorithms.RSASHA512(),
                KeyAlgorithms.RSASHA256(),
            )
        },
    ).apply {
        addHostKeyVerifier(verifier)
        connectTimeout = TIMEOUT_MS
        timeout = TIMEOUT_MS
    }

    private fun keyProvider(): KeyProvider = object : KeyProvider {
        override fun getPrivate(): PrivateKey = keys.privateKey()
        override fun getPublic(): PublicKey = keys.publicKey()
        override fun getType(): KeyType = KeyType.ECDSA256
    }

    private fun fingerprintOf(key: PublicKey): String =
        OpenSshKey.fingerprint(Buffer.PlainBuffer().putPublicKey(key).compactData)

    private companion object {
        const val TIMEOUT_MS = 5_000
        const val COMMAND_WAIT_SECONDS = 5L
        const val MAX_ERROR_BYTES = 4096
    }
}
