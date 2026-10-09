package com.diegobergonsi.wakedeck.widget

import androidx.annotation.DrawableRes
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.widget.config.WidgetIcon

@DrawableRes
fun WidgetIcon.drawableRes(): Int = when (this) {
    WidgetIcon.POWER -> R.drawable.ic_widget_power
    WidgetIcon.DESKTOP -> R.drawable.ic_widget_desktop
    WidgetIcon.LAPTOP -> R.drawable.ic_widget_laptop
    WidgetIcon.SERVER -> R.drawable.ic_widget_server
    WidgetIcon.TV -> R.drawable.ic_widget_tv
    WidgetIcon.GAMEPAD -> R.drawable.ic_widget_gamepad
    WidgetIcon.HOME -> R.drawable.ic_widget_home
    WidgetIcon.BOLT -> R.drawable.ic_widget_bolt
    WidgetIcon.CLOUD -> R.drawable.ic_widget_cloud
    WidgetIcon.ROUTER -> R.drawable.ic_widget_router
    WidgetIcon.STAR -> R.drawable.ic_widget_star
    WidgetIcon.CHIP -> R.drawable.ic_widget_chip
}
