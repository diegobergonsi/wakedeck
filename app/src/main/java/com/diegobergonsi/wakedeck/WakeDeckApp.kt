package com.diegobergonsi.wakedeck

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.diegobergonsi.wakedeck.shortcuts.ShortcutPublisher
import com.diegobergonsi.wakedeck.widget.WidgetPreviews
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WakeDeckApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var shortcutPublisher: ShortcutPublisher

    override fun onCreate() {
        super.onCreate()
        shortcutPublisher.start()
        CoroutineScope(Dispatchers.Default).launch { runCatching { WidgetPreviews.publishIfNeeded(this@WakeDeckApp) } }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
