package com.diegobergonsi.wakedeck.di

import android.content.Context
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.WakeCoordinator
import com.diegobergonsi.wakedeck.tile.TileStore
import com.diegobergonsi.wakedeck.data.widget.StatusStore
import com.diegobergonsi.wakedeck.data.widget.WidgetConfigStore
import com.diegobergonsi.wakedeck.domain.StatusChecker
import com.diegobergonsi.wakedeck.domain.WakeOnLan
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Hilt access for classes the framework creates itself: Glance widgets, action callbacks, receivers. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun deviceRepository(): DeviceRepository
    fun wakeOnLan(): WakeOnLan
    fun statusChecker(): StatusChecker
    fun configStore(): WidgetConfigStore
    fun statusStore(): StatusStore
    fun wakeCoordinator(): WakeCoordinator
    fun tileStore(): TileStore
}

fun Context.widgetEntryPoint(): WidgetEntryPoint =
    EntryPointAccessors.fromApplication(applicationContext, WidgetEntryPoint::class.java)
