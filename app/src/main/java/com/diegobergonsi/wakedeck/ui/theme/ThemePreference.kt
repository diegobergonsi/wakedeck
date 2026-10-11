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

    var mode by mutableStateOf(AppThemeMode.SYSTEM)
        private set

    fun load(context: Context) {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
        mode = AppThemeMode.entries.firstOrNull { it.name == saved } ?: AppThemeMode.SYSTEM
    }

    fun set(context: Context, value: AppThemeMode) {
        mode = value
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, value.name).apply()
    }
}
