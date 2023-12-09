package org.scarlet.android.hotflow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HotFlowViewModel : ViewModel() {

    private val _backingFlow = MutableStateFlow(-1)
    lateinit var flow: StateFlow<Int>

    private fun cancelJobs() {
        viewModelScope.coroutineContext.job.cancelChildren()
    }

    fun startFlow(kind: FlowKind) {
        cancelJobs()
        when (kind) {
            // Plain StateFlow
            FlowKind.FLOW1 -> {
                flow = _backingFlow
                viewModelScope.launch {
                    repeat(Int.MAX_VALUE) {
                        Log.v(TAG, "ViewModel: value = $it")
                        _backingFlow.value = it
                        delay(2_000)
                    }
                }
            }

            // Take advantage of `subscriptionCount` to avoid unnecessary work
            FlowKind.FLOW2 -> {
                val channel = Channel<Unit>()
                var oldCount = 0
                var trigger = false
                viewModelScope.launch {
                    _backingFlow.subscriptionCount.collect {
                        if (oldCount == 0 && it != 0) {
                            trigger = true
                        }
                        if (trigger) {
                            channel.send(Unit)
                            trigger = false
                        }
                        oldCount = it
                    }
                }
                flow = _backingFlow
                viewModelScope.launch {
                    repeat(Int.MAX_VALUE) {
                        if (_backingFlow.subscriptionCount.value != 0) {
                            _backingFlow.value = it
                            Log.v(TAG, "ViewModel: value = $it")
                            delay(2_000)
                        } else {
                            Log.v(TAG, "Pause emission ...")
                            channel.receive()
                            Log.v(TAG, "Resume emission ...")
                        }
                    }
                }
            }
            // Using `stateIn`
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