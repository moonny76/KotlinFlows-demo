package org.scarlet.android.collect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HotFlowViewModel : ViewModel() {

    private val _backingFlow = MutableStateFlow(-1)
    lateinit var flow: StateFlow<Int>

    fun startFlow(kind: FlowKind) {
        when (kind) {
            FlowKind.FLOW1 -> {
                flow = _backingFlow.asStateFlow()
                viewModelScope.launch {
                    repeat(Int.MAX_VALUE) {
                        Log.v(TAG, "ViewModel: value = $it")
                        _backingFlow.value = it
                        delay(2_000)
                    }
                }
            }
            FlowKind.FLOW2 -> {
                flow = _backingFlow.asStateFlow()
                viewModelScope.launch {
                    repeat(Int.MAX_VALUE) {
                        if (_backingFlow.subscriptionCount.value != 0) {
                            Log.v(TAG, "ViewModel: value = $it")
                            _backingFlow.value = it
                        } else {
                            Log.v(TAG, "ViewModel: looping ...")
                        }
                        delay(2_000)
                    }
                }
            }
            FlowKind.FLOW3 -> {
                flow = flow {
                    repeat(Int.MAX_VALUE) {
                        Log.v(TAG, "ViewModel: value = $it")
                        emit(it)
                        delay(2_000)
                    }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = -1
                )
            }
        }
    }

    companion object {
        private const val TAG = "Producer"

        enum class FlowKind { FLOW1, FLOW2, FLOW3 }
    }
}