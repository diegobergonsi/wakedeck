package com.diegobergonsi.wakedeck.ui.devices

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** Shows the 12 stored hex digits as AA:BB:CC:DD:EE:FF. The colons are display only, so the cursor and deletes work on the digits. */
object MacVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val shown = digits.chunked(2).joinToString(":")
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = if (offset <= 0) 0 else offset + (offset - 1) / 2

            override fun transformedToOriginal(offset: Int) = (offset - offset / 3).coerceAtMost(digits.length)
        }
        return TransformedText(AnnotatedString(shown), mapping)
    }
}
