package com.diegobergonsi.wakedeck.ui.theme

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.diegobergonsi.wakedeck.R

@Composable
fun ThemeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val labels = mapOf(
        AppThemeMode.SYSTEM to R.string.language_system,
        AppThemeMode.LIGHT to R.string.theme_light,
        AppThemeMode.DARK to R.string.theme_dark,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.config_theme)) },
        text = {
            Column {
                AppThemeMode.entries.forEach { mode ->
                    val choose = { ThemePreference.set(context, mode); onDismiss() }
                    Row(
                        Modifier.fillMaxWidth().clickable(onClick = choose).padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = ThemePreference.mode == mode, onClick = choose)
                        Text(stringResource(labels.getValue(mode)), Modifier.padding(start = 8.dp))
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.theme_wallpaper_colors))
                            Text(
                                stringResource(R.string.theme_wallpaper_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = ThemePreference.wallpaperColors, onCheckedChange = { ThemePreference.setWallpaperColors(context, it) })
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
