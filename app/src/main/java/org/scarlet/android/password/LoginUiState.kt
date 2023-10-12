package org.scarlet.android.password

sealed class LoginUiState {
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    data object Loading : LoginUiState()
    data object Empty : LoginUiState()
}