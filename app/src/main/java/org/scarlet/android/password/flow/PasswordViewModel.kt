package org.scarlet.android.password.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PasswordViewModel : ViewModel() {

    /* Password: StateFlow */

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

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

    /* Event Counter: Flow */

    private var currentCount = 0
    private val _countChannel = Channel<Int>(Channel.BUFFERED)
    val countFlow: Flow<Int> = _countChannel.receiveAsFlow()

    @ObsoleteCoroutinesApi
    val eventChannel = viewModelScope.actor<Unit> {
        _countChannel.send(currentCount)
        consumeEach {
            _countChannel.send(++currentCount)
        }
    }

//    init {
//        viewModelScope.launch {
//            handleEvent()
//        }
//    }

//    private suspend fun handleEvent() {
//        _countChannel.send(currentCount)
//        eventChannel.consumeEach { event ->
//            _countChannel.send(++currentCount)
//        }
//    }

//    override fun onCleared() {
//        super.onCleared()
//        eventChannel.close()
//        _countChannel.close()
//    }

    companion object {
        const val TAG = "Password"
    }
}