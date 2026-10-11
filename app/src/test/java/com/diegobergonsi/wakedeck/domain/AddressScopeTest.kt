package com.diegobergonsi.wakedeck.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressScopeTest {
    @Test fun privateIpv4IsLocal() {
        listOf("192.168.1.255", "10.0.0.4", "172.16.0.1", "172.31.255.1", "169.254.1.1", "127.0.0.1", "255.255.255.255", "100.101.5.6")
            .forEach { assertTrue(it, AddressScope.isLocal(it)) }
    }

    @Test fun publicIpv4IsNotLocal() {
        listOf("8.8.8.8", "172.32.0.1", "172.15.0.1", "100.128.0.1", "1.2.3.4", "192.169.1.1")
            .forEach { assertFalse(it, AddressScope.isLocal(it)) }
    }

    @Test fun hostNames() {
        assertTrue(AddressScope.isLocal("my-pc"))
        assertTrue(AddressScope.isLocal("My-PC.local"))
        assertTrue(AddressScope.isLocal("nas.home.arpa"))
        assertFalse(AddressScope.isLocal("example.com"))
        assertFalse(AddressScope.isLocal("pc.evil.example"))
    }

    @Test fun ipv6() {
        assertTrue(AddressScope.isLocal("fe80::1"))
        assertTrue(AddressScope.isLocal("fd12:3456::1"))
        assertFalse(AddressScope.isLocal("2001:db8::1"))
    }

    @Test fun emptyIsLocal() = assertTrue(AddressScope.isLocal(""))

    @Test fun malformedNumericIsNotLocal() = assertFalse(AddressScope.isLocal("999.1.1"))
}
