package com.diegobergonsi.wakedeck.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

/** The app's own light/dark choice. [AppThemeMode.SYSTEM] follows the phone, including its automatic schedule. */
object ThemePreference {
    private const val PREFS = "app_prefs"
    private const val KEY = "theme_mode"
    private const val KEY_WALLPAPER = "wallpaper_colors"

    var mode by mutableStateOf(AppThemeMode.SYSTEM)
        private set

    /** Android 12+ only: use the wallpaper's Material You colors instead of the WakeDeck blue. Off by default. */
    var wallpaperColors by mutableStateOf(false)
        private set

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        mode = AppThemeMode.entries.firstOrNull { it.name == prefs.getString(KEY, null) } ?: AppThemeMode.SYSTEM
        wallpaperColors = prefs.getBoolean(KEY_WALLPAPER, false)
    }

    fun setWallpaperColors(context: Context, value: Boolean) {
        wallpaperColors = value
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_WALLPAPER, value).apply()
    }

    fun set(context: Context, value: AppThemeMode) {
        mode = value
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, value.name).apply()
    }
}
