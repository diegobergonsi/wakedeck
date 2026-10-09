package com.diegobergonsi.wakedeck.widget

import org.junit.Assert.assertEquals
import org.junit.Test

class WakeRecordTest {
    @Test
    fun successShowsSentWhileFresh() {
        val r = WakeRecord(timestampMs = 10_000, success = true)
        assertEquals(WakeFeedback.SENT, r.feedbackAt(10_000))
        assertEquals(WakeFeedback.SENT, r.feedbackAt(20_000))
    }

    @Test
    fun expiresAfterStaleWindowEvenIfNeverCleared() {
        val r = WakeRecord(10_000, true)
        assertEquals(WakeFeedback.NONE, r.feedbackAt(20_001))
    }

    @Test
    fun failureShowsFailed() {
        assertEquals(WakeFeedback.FAILED, WakeRecord(5_000, false).feedbackAt(6_000))
    }

    @Test
    fun clockGoingBackwardsIsNone() {
        assertEquals(WakeFeedback.NONE, WakeRecord(5_000, true).feedbackAt(4_000))
    }
}
