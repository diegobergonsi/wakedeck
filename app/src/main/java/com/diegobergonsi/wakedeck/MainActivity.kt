package com.diegobergonsi.wakedeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.diegobergonsi.wakedeck.ui.WakeDeckNav
import com.diegobergonsi.wakedeck.ui.theme.WakeDeckTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WakeDeckTheme { WakeDeckNav() }
        }
    }
}
