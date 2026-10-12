package com.diegobergonsi.wakedeck.widget

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetContrastTest {
    // A sweep of the RGB cube, so the black-or-white rule is checked on every kind of user-picked color.
    private val colors = (0..255 step 51).flatMap { r -> (0..255 step 51).flatMap { g -> (0..255 step 51).map { b -> Color(r, g, b) } } }

    @Test
    fun bestOnAlwaysReadsAtTextContrast() {
        colors.forEach { assertTrue("$it", contrast(it, bestOn(it)) >= MIN_TEXT_CONTRAST) }
    }

    @Test
    fun softTextNeverDropsBelowTextContrast() {
        colors.forEach { assertTrue("$it", contrast(it, softText(it)) >= MIN_TEXT_CONTRAST) }
    }
}
