package com.diegobergonsi.wakedeck.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Icon
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.di.ApplicationScope
import com.diegobergonsi.wakedeck.domain.Device
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Keeps the launcher's long-press menu in sync with the devices marked "show in launcher menu". */
@Singleton
class ShortcutPublisher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val devices: DeviceRepository,
    @ApplicationScope private val scope: CoroutineScope,
) {
    fun start() {
        scope.launch {
            devices.observeAll()
                .map { ShortcutSelection.pick(it).map { d -> d.id to d.name } }
                .distinctUntilChanged()
                .collect { picked -> runCatching { publish(picked) } }
        }
    }

    private fun publish(picked: List<Pair<Long, String>>) {
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return
        val icon = Icon.createWithAdaptiveBitmap(renderIcon())
        manager.dynamicShortcuts = picked.map { (id, name) ->
            ShortcutInfo.Builder(context, "wake-$id")
                .setShortLabel(name)
                .setLongLabel(context.getString(R.string.shortcut_wake_long, name))
                .setIcon(icon)
                .setIntent(
                    Intent(context, WakeShortcutActivity::class.java)
                        .setAction(WakeShortcutActivity.ACTION_WAKE)
                        .putExtra(WakeShortcutActivity.EXTRA_DEVICE_ID, id),
                )
                .build()
        }
    }

    /** Power icon on the app's blue, drawn into the safe zone of an adaptive icon. */
    private fun renderIcon(): Bitmap {
        val size = 432
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ACCENT)
        context.getDrawable(R.drawable.ic_widget_power)?.mutate()?.let { d ->
            d.setTint(Color.WHITE)
            val inset = size * 0.28f
            d.setBounds(inset.toInt(), inset.toInt(), (size - inset).toInt(), (size - inset).toInt())
            d.draw(canvas)
        }
        return bitmap
    }

    private companion object {
        val ACCENT = 0xFF4A5B8F.toInt()
    }
}
