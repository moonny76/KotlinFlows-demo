package org.scarlet.android.password.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.scarlet.android.password.LoginUiState

class PasswordViewModel : ViewModel() {

    private val _loginUiState = MutableLiveData<LoginUiState>(LoginUiState.Empty)
    val loginUiState: LiveData<LoginUiState> = _loginUiState

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

    private val _counter = MutableLiveData(0)
    val counter: LiveData<Int> = _counter

    fun increment() {
        _counter.value = _counter.value?.plus(1)
    }
}