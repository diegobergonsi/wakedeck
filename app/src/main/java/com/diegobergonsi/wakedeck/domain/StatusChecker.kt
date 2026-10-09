package com.diegobergonsi.wakedeck.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

/**
 * Online/offline check. With a status port: TCP connect. Without: ping via [InetAddress.isReachable].
 * A refused TCP connection counts as online: the machine answered with a reset, so it is up.
 */
class StatusChecker @Inject constructor() {
    suspend fun check(device: Device, timeoutMs: Int = DEFAULT_TIMEOUT_MS): DeviceStatus {
        val host = device.host.trim()
        if (host.isEmpty()) return DeviceStatus.UNAVAILABLE
        return withContext(Dispatchers.IO) {
            val port = device.statusPort
            if (port != null) tcp(host, port, timeoutMs) else ping(host, timeoutMs)
        }
    }

    private fun tcp(host: String, port: Int, timeoutMs: Int): DeviceStatus = try {
        Socket().use { it.connect(InetSocketAddress(host, port), timeoutMs) }
        DeviceStatus.ONLINE
    } catch (e: ConnectException) {
        if (isRefused(e)) DeviceStatus.ONLINE else DeviceStatus.OFFLINE
    } catch (e: IOException) {
        DeviceStatus.OFFLINE
    }

    private fun ping(host: String, timeoutMs: Int): DeviceStatus = try {
        if (InetAddress.getByName(host).isReachable(timeoutMs)) DeviceStatus.ONLINE else DeviceStatus.OFFLINE
    } catch (e: IOException) {
        DeviceStatus.OFFLINE
    }

    private fun isRefused(e: ConnectException): Boolean {
        val message = e.message.orEmpty()
        return message.contains("refused", ignoreCase = true) || message.contains("ECONNREFUSED")
    }

    companion object {
        const val DEFAULT_TIMEOUT_MS = 1500
    }
}
