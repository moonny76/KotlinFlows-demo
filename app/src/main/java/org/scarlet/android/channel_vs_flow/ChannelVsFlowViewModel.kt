package org.scarlet.android.channel_vs_flow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChannelVsFlowViewModel : ViewModel() {

    private val _sharedFlow = MutableSharedFlow<Int>()
    var sharedFlow = _sharedFlow.asSharedFlow()

    private val _channel = Channel<Int>()
    val channelFlow = _channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            repeat(Int.MAX_VALUE) {
                _sharedFlow.emit(it)
                Log.i(TAG, "SharedFlow: emitted = $it")
                delay(1000)
            }
        }

        viewModelScope.launch {
            repeat(Int.MAX_VALUE) {
                _channel.send(it)
                Log.d(TAG, "ChannelVsFlowViewModel: sent = $it")
                delay(1000)
            }
        }
        Log.d(TAG, "View Model created ... ")
    }

    companion object {
        private const val TAG = "ChannelVsFlow"
    }
}