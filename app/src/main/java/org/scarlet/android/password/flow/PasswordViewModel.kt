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
            if (username == "android" && password == "topsecret") {
                _loginUiState.value = LoginUiState.Success
//                _isError.emit(false)
            } else {
                _loginUiState.value = LoginUiState.Error("Wrong credentials")
//                _isError.emit(true)
            }
        }
    }

    private val _counterFlow = MutableStateFlow(0)
    val counterFlow = _counterFlow.asStateFlow()

    fun increment() {
        _counterFlow.value += 1
    }

//    private val _isError = MutableSharedFlow<Boolean>()
//    val isError = _isError.asSharedFlow()

    val isError: SharedFlow<Boolean> = (loginUiState as Flow<LoginUiState>)
        .filter { it is LoginUiState.Error || it is LoginUiState.Success }
        .map {
            when (it) {
                is LoginUiState.Error -> true
                else -> false
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 0
        )
}