package org.scarlet.android.password.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.scarlet.android.password.LoginUiState

class PasswordViewModel : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    fun login(username: String, password: String) {
        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            delay(1000L)
            if (username == "android" && password == "abc") {
                _loginUiState.value = LoginUiState.Success
            } else {
                _loginUiState.value = LoginUiState.Error("Wrong credentials")
            }
        }
    }

    fun done() {
        _loginUiState.value = LoginUiState.Empty
    }

    private val _counterFlow = MutableStateFlow(0)
    val counterFlow = _counterFlow.asStateFlow()

    fun increment() {
        _counterFlow.value += 1
    }

}