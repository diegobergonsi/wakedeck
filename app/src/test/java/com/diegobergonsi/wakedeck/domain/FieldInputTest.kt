package com.diegobergonsi.wakedeck.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FieldInputTest {
    @Test fun macDropsNonHexAndCapitalises() = assertEquals("AA:BB::CC", FieldInput.mac("aa:bb:zz:cc!"))

    @Test fun macStopsAtSeventeenCharacters() = assertEquals(17, FieldInput.mac("AAAAAAAAAAAAAAAAAAAAAAAA").length)

    @Test fun macRemovesPastedSpaces() = assertEquals("AABBCCDDEEFF", FieldInput.mac("aa bb cc dd ee ff"))

    @Test fun portKeepsFiveDigits() = assertEquals("12345", FieldInput.port("1-2.3,4 5 6"))

    @Test fun hostDropsSpacesAndSymbols() = assertEquals("my-pc.local", FieldInput.host("my-pc .local!/"))

    @Test fun sshUserDropsWhitespace() = assertEquals("deck", FieldInput.sshUser(" d e\tck "))
}
