package com.diegobergonsi.wakedeck.widget

import com.diegobergonsi.wakedeck.widget.config.DensityOption
import com.diegobergonsi.wakedeck.widget.config.ListRowStyle
import com.diegobergonsi.wakedeck.widget.config.TextSizeOption
import com.diegobergonsi.wakedeck.widget.config.WidgetConfig
import com.diegobergonsi.wakedeck.widget.config.WidgetConfigCodec
import com.diegobergonsi.wakedeck.widget.config.WidgetIcon
import com.diegobergonsi.wakedeck.widget.config.WidgetThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WidgetConfigCodecTest {
    @Test
    fun roundTripKeepsEveryField() {
        val config = WidgetConfig(
            deviceIds = listOf(3, 7, 12),
            themeMode = WidgetThemeMode.DARK,
            backgroundColor = 0xFF112233.toInt(),
            accentColor = 0xFFAABBCC.toInt(),
            backgroundAlpha = 0.4f,
            cornerRadiusDp = 12,
            icon = WidgetIcon.SERVER,
            label = "Office, PC | main",
            showStatus = false,
            showRefresh = false,
            textSize = TextSizeOption.LARGE,
            density = DensityOption.COMPACT,
            rowStyle = ListRowStyle.MINIMAL,
            gridColumns = 3,
            onlineColor = 0xFF00AA00.toInt(),
            offlineColor = 0xFFAA0000.toInt(),
            sentColor = 0xFF0000AA.toInt(),
            borderWidthDp = 2,
            borderColor = 0xFFCCCCCC.toInt(),
        )
        assertEquals(config, WidgetConfigCodec.decode(WidgetConfigCodec.encode(config)))
    }

    @Test
    fun emptyMapGivesDefaults() {
        assertEquals(WidgetConfig(), WidgetConfigCodec.decode(emptyMap()))
    }

    @Test
    fun badValuesFallBackToDefaults() {
        val decoded = WidgetConfigCodec.decode(
            mapOf(
                "deviceIds" to "1,abc,,5",
                "theme" to "NEON",
                "background" to "red",
                "alpha" to "nope",
                "radius" to "x",
                "icon" to "???",
                "showStatus" to "maybe",
                "showRefresh" to "perhaps",
                "textSize" to "HUGE",
                "density" to "TIGHT",
                "rowStyle" to "FANCY",
                "gridColumns" to "many",
            ),
        )
        assertEquals(listOf(1L, 5L), decoded.deviceIds)
        assertEquals(WidgetThemeMode.MATERIAL_YOU, decoded.themeMode)
        assertNull(decoded.backgroundColor)
        assertEquals(1f, decoded.backgroundAlpha)
        assertEquals(WidgetConfig.DEFAULT_CORNER_RADIUS_DP, decoded.cornerRadiusDp)
        assertEquals(WidgetIcon.POWER, decoded.icon)
        assertEquals(true, decoded.showStatus)
        assertEquals(true, decoded.showRefresh)
        assertEquals(TextSizeOption.MEDIUM, decoded.textSize)
        assertEquals(DensityOption.COMFORTABLE, decoded.density)
        assertEquals(ListRowStyle.DETAILED, decoded.rowStyle)
        assertEquals(0, decoded.gridColumns)
    }

    @Test
    fun outOfRangeNumbersAreClamped() {
        val decoded = WidgetConfigCodec.decode(mapOf("alpha" to "7", "radius" to "999", "gridColumns" to "99", "borderWidth" to "50"))
        assertEquals(1f, decoded.backgroundAlpha)
        assertEquals(WidgetConfig.MAX_CORNER_RADIUS_DP, decoded.cornerRadiusDp)
        assertEquals(WidgetConfig.MAX_GRID_COLUMNS, decoded.gridColumns)
        assertEquals(WidgetConfig.MAX_BORDER_WIDTH_DP, decoded.borderWidthDp)
        assertEquals(0f, WidgetConfigCodec.decode(mapOf("alpha" to "-3")).backgroundAlpha)
    }

    @Test
    fun nullColorsAreNotEncoded() {
        val encoded = WidgetConfigCodec.encode(WidgetConfig())
        assertNull(encoded["background"])
        assertNull(encoded["accent"])
        assertNull(encoded["onlineColor"])
        assertNull(encoded["borderColor"])
    }
}
