package org.scarlet.android.password.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PasswordViewModel : ViewModel() {

    /* Password */

    private val _loginUiState = MutableLiveData<LoginUiState>(LoginUiState.Empty)
    val loginUiState: LiveData<LoginUiState> = _loginUiState

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginUiState.value = LoginUiState.Loading
        delay(2000L)
        if(username == "android" && password == "topsecret") {
            _loginUiState.value = LoginUiState.Success
        } else {
            _loginUiState.value = LoginUiState.Error("Wrong credentials")
        }
    }

    sealed class LoginUiState {
        object Success : LoginUiState()
        data class Error(val message: String) : LoginUiState()
        object Loading : LoginUiState()
        object Empty : LoginUiState()
    }

    /* Click Counter */

    val eventChannel = Channel<Unit>(Channel.UNLIMITED)

    private var currentCount = 0
    val countChannel = viewModelScope.produce {
        send(currentCount)
        eventChannel.consumeEach { event ->
            this@produce.send(++currentCount)
        }
    }

    companion object {
        const val TAG = "Password"
    }

    override fun onCleared() {
        super.onCleared()
        eventChannel.close()
    }
}