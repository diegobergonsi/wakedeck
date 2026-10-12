package com.diegobergonsi.wakedeck.ui.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.diegobergonsi.wakedeck.R

/** Languages the app is translated into. An empty tag means "follow the system". */
private data class AppLanguage(val tag: String, @StringRes val name: Int)

private val languages = listOf(
    AppLanguage("en", R.string.language_en),
    AppLanguage("pt-BR", R.string.language_pt_br),
    AppLanguage("pt-PT", R.string.language_pt_pt),
    AppLanguage("fr", R.string.language_fr),
    AppLanguage("de", R.string.language_de),
    AppLanguage("es", R.string.language_es),
    AppLanguage("zh", R.string.language_zh),
    AppLanguage("ja", R.string.language_ja),
    AppLanguage("it", R.string.language_it),
    AppLanguage("ru", R.string.language_ru),
    AppLanguage("ko", R.string.language_ko),
    AppLanguage("ar", R.string.language_ar),
    AppLanguage("tr", R.string.language_tr),
    AppLanguage("vi", R.string.language_vi),
    AppLanguage("id", R.string.language_id),
    AppLanguage("pl", R.string.language_pl),
    AppLanguage("sv", R.string.language_sv),
    AppLanguage("da", R.string.language_da),
    AppLanguage("nb", R.string.language_nb),
    AppLanguage("fi", R.string.language_fi),
)

/** Per-app language needs the system feature that arrived with Android 13. */
val perAppLanguageSupported: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    val manager = LocalContext.current.getSystemService(LocaleManager::class.java)
    val current = manager.applicationLocales.toLanguageTags()
    val choose = { tag: String ->
        onDismiss()
        // Changing the locale restarts the activity with the new language.
        manager.applicationLocales = if (tag.isEmpty()) LocaleList.getEmptyLocaleList() else LocaleList.forLanguageTags(tag)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.menu_language)) },
        text = {
            androidx.compose.foundation.layout.Column(Modifier.verticalScroll(rememberScrollState())) {
                LanguageRow(stringResource(R.string.language_system), current.isEmpty()) { choose("") }
                languages.forEach { lang ->
                    LanguageRow(stringResource(lang.name), current.equals(lang.tag, ignoreCase = true)) { choose(lang.tag) }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

@Composable
private fun LanguageRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, Modifier.padding(start = 8.dp))
    }
}
