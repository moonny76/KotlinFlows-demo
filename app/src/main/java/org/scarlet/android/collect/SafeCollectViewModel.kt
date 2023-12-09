package org.scarlet.android.collect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SafeCollectViewModel : ViewModel() {
    private val _numbers = MutableStateFlow(-1)
    val numbers = _numbers.asStateFlow()

    private var current = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                _numbers.value = current++
                delay(FAKE_NETWORK_DELAY)
            }
        }
    }

    companion object {
        const val FAKE_NETWORK_DELAY = 2_000L
        const val TAG = "Collect"
    }
}