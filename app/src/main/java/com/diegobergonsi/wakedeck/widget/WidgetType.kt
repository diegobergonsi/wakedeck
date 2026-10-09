package com.diegobergonsi.wakedeck.widget

enum class WidgetType {
    SINGLE, LIST, GRID;

    companion object {
        fun fromReceiverClass(name: String?): WidgetType = when (name) {
            DeviceListWidgetReceiver::class.java.name -> LIST
            DeviceGridWidgetReceiver::class.java.name -> GRID
            else -> SINGLE
        }
    }
}
