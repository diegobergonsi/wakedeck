package com.diegobergonsi.wakedeck.ui.ssh

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

@HiltViewModel
class SshKeyViewModel @Inject constructor(private val keys: SshKeyManager) : ViewModel() {
    var publicKeyLine by mutableStateOf("")
        private set
    var fingerprint by mutableStateOf("")
        private set

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                keys.ensureKey()
                publicKeyLine = keys.authorizedKeysLine()
                fingerprint = keys.fingerprint()
            }
        }
    }

    fun regenerate() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) { keys.deleteKey() }
            refresh()
        }
    }
}
