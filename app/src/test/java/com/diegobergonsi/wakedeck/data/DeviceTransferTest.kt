package com.diegobergonsi.wakedeck.data

import com.diegobergonsi.wakedeck.data.transfer.DecodeResult
import com.diegobergonsi.wakedeck.data.transfer.DeviceTransfer
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.Group
import com.diegobergonsi.wakedeck.domain.OsType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceTransferTest {
    private val office = Group(7, "Office")
    private val pc = Device(
        id = 1, name = "Main PC", mac = "00:E0:21:9B:80:C9", host = "192.168.1.235",
        broadcastAddress = "192.168.1.255", port = 9, os = OsType.WINDOWS, statusPort = 3389,
        groupId = 7, showInLauncher = true, sshUser = "me", sshPort = 2222, sshHostKey = "SHA256:secretish",
    )

    private fun decode(text: String) = (DeviceTransfer.decode(text) as DecodeResult.Success)

    @Test
    fun roundTripKeepsEveryExportedField() {
        val result = decode(DeviceTransfer.encode(listOf(pc), listOf(office)))
        val d = result.devices.single()
        assertEquals("Main PC", d.name)
        assertEquals("00:E0:21:9B:80:C9", d.mac)
        assertEquals("192.168.1.235", d.host)
        assertEquals("192.168.1.255", d.broadcastAddress)
        assertEquals(OsType.WINDOWS, d.os)
        assertEquals(3389, d.statusPort)
        assertEquals("Office", d.group)
        assertTrue(d.showInLauncher)
        assertEquals("me", d.sshUser)
        assertEquals(2222, d.sshPort)
        assertEquals(0, result.invalid)
    }

    @Test
    fun exportHasNoSecrets() {
        val text = DeviceTransfer.encode(listOf(pc), listOf(office))
        assertFalse(text.contains("secretish"))
        assertFalse(text.contains("sshHostKey", ignoreCase = true))
        assertFalse(text.contains("privateKey", ignoreCase = true))
    }

    @Test
    fun deviceWithoutGroupHasNoGroupField() {
        val text = DeviceTransfer.encode(listOf(pc.copy(groupId = null)), listOf(office))
        assertEquals(null, decode(text).devices.single().group)
    }

    @Test
    fun badEntriesAreCountedAndSkipped() {
        val text = """{"app":"WakeDeck","version":1,"devices":[
            {"name":"ok","mac":"aa:bb:cc:dd:ee:ff"},
            {"name":"","mac":"aa:bb:cc:dd:ee:00"},
            {"name":"badmac","mac":"nope"},
            {"name":"badport","mac":"aa:bb:cc:dd:ee:11","port":70000},
            "junk"]}"""
        val result = decode(text)
        assertEquals(listOf("ok"), result.devices.map { it.name })
        assertEquals(4, result.invalid)
        assertEquals(Device.DEFAULT_BROADCAST, result.devices.single().broadcastAddress)
        assertEquals("AA:BB:CC:DD:EE:FF", result.devices.single().mac)
    }

    @Test
    fun notAWakeDeckFileIsRejected() {
        assertTrue(DeviceTransfer.decode("hello") is DecodeResult.Failure)
        assertTrue(DeviceTransfer.decode("""{"app":"Other","devices":[]}""") is DecodeResult.Failure)
        assertTrue(DeviceTransfer.decode("""{"app":"WakeDeck","version":99,"devices":[]}""") is DecodeResult.Failure)
        assertTrue(DeviceTransfer.decode("""{"app":"WakeDeck","version":1}""") is DecodeResult.Failure)
    }

    @Test
    fun importSkipsExistingMacsAndRepeatsInTheFile() {
        val text = DeviceTransfer.encode(
            listOf(pc, pc.copy(id = 2, name = "Same MAC lower", mac = "00:e0:21:9b:80:c9"), pc.copy(id = 3, name = "New", mac = "11:22:33:44:55:66")),
            listOf(office),
        )
        val incoming = decode(text)
        val existing = listOf(Device(id = 9, name = "Already", mac = "11:22:33:44:55:66"))
        val plan = DeviceTransfer.plan(existing, incoming.devices, incoming.invalid)
        assertEquals(listOf("Main PC"), plan.toAdd.map { it.name })
        assertEquals(2, plan.skippedExisting)
    }
}
