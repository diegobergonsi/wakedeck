package com.diegobergonsi.wakedeck.widget

enum class WakeFeedback { NONE, SENT, FAILED }

/** Result of the last wake tap for one device. The wake action clears it after [FEEDBACK_MS]. */
data class WakeRecord(val timestampMs: Long, val success: Boolean) {
    /** Safety net: if the clear never ran (process killed), the feedback still expires after [STALE_MS]. */
    fun feedbackAt(nowMs: Long): WakeFeedback = when {
        nowMs - timestampMs !in 0..STALE_MS -> WakeFeedback.NONE
        success -> WakeFeedback.SENT
        else -> WakeFeedback.FAILED
    }

    companion object {
        const val FEEDBACK_MS = 3_000L
        const val STALE_MS = 10_000L
    }
}
