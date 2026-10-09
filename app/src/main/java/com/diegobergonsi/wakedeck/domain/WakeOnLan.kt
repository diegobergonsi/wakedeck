package com.diegobergonsi.wakedeck.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

class WakeOnLan @Inject constructor() {
    suspend fun send(device: Device): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val mac = requireNotNull(MagicPacket.parseMac(device.mac)) { "Invalid MAC" }
            val payload = MagicPacket.build(mac)
            val target = InetAddress.getByName(device.broadcastAddress)
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.send(DatagramPacket(payload, payload.size, target, device.port))
            }
        }
    }
}
