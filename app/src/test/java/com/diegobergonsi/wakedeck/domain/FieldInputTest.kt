package com.diegobergonsi.wakedeck.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FieldInputTest {
    @Test fun macKeepsOnlyHexDigitsInCapitals() = assertEquals("AABBCC", FieldInput.mac("aa:bb:zz:cc!"))

    @Test fun macStopsAtTwelveDigits() = assertEquals("AAAAAAAAAAAA", FieldInput.mac("AAAAAAAAAAAAAAAAAAAAAAAA"))

    @Test fun macAcceptsAPastedMacWithAnySeparator() {
        assertEquals("AABBCCDDEEFF", FieldInput.mac("aa bb cc dd ee ff"))
        assertEquals("AABBCCDDEEFF", FieldInput.mac("aa-bb-cc-dd-ee-ff"))
        assertEquals("AABBCCDDEEFF", FieldInput.mac("aabb.ccdd.eeff"))
    }

    @Test fun macTextWithTooManyDigitsOrColonsLosesTheExtras() = assertEquals("AA8282828287", FieldInput.mac("AA:828282828:7272"))

    @Test fun portKeepsFiveDigits() = assertEquals("12345", FieldInput.port("1-2.3,4 5 6"))

    @Test fun hostDropsSpacesAndSymbols() = assertEquals("my-pc.local", FieldInput.host("my-pc .local!/"))

    @Test fun sshUserDropsWhitespace() = assertEquals("deck", FieldInput.sshUser(" d e\tck "))
}
