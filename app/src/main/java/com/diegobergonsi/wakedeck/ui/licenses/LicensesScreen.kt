package com.diegobergonsi.wakedeck.ui.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.diegobergonsi.wakedeck.R
import com.diegobergonsi.wakedeck.ui.contentSidePadding

private const val DIVIDER = "===="

/** The third-party notices the libraries' licenses ask for. The text lives in res/raw/open_source_licenses.txt. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val (summary, licenseTexts) = remember {
        val raw = context.resources.openRawResource(R.raw.open_source_licenses).bufferedReader().use { it.readText() }
        // The license texts have hard line breaks meant for wide screens. Join each paragraph so it wraps to the phone.
        val split = raw.indexOf(DIVIDER)
        raw.substring(0, split).trim() to raw.substring(split).trim().split("\n\n").joinToString("\n\n") { p ->
            // Keep divider and title lines as they are; only join ordinary paragraphs.
            if (p.startsWith(DIVIDER)) p else p.lines().joinToString(" ") { it.trim() }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_licenses)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = contentSidePadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.licenses_intro), style = MaterialTheme.typography.bodyMedium)
            Text(summary, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
            Text(licenseTexts, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(24.dp))
        }
    }
}
