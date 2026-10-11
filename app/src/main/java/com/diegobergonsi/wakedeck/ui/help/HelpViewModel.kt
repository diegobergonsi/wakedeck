package com.diegobergonsi.wakedeck.ui.help

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.ssh.SshKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Gives the Help screen the app's real SSH public key, so the setup commands are ready to paste. */
@HiltViewModel
class HelpViewModel @Inject constructor(private val keys: SshKeyManager) : ViewModel() {
    var publicKeyLine by mutableStateOf("")
        private set

    /** The Linux version carries a forced command, see [SshKeyManager.linuxAuthorizedKeysLine]. */
    var linuxKeyLine by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                keys.ensureKey()
                keys.authorizedKeysLine() to keys.linuxAuthorizedKeysLine()
            }.let { (plain, linux) ->
                publicKeyLine = plain
                linuxKeyLine = linux
            }
        }
    }
}
