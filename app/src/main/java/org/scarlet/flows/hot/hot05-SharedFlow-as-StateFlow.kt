package org.scarlet.flows.hot

import kotlinx.coroutines.*
import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.scarlet.util.delim
import org.scarlet.util.log

/**
 * ## Make SharedFlow as StateFLow
 */
object SharedFlow_As_StateFlow {

    private val _stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)
    val stateFlow: StateFlow<Resource<Int>> = _stateFlow

    // MutableStateFlow(initialValue) is a shared flow with the following parameters:
    private val _sharedFlow = MutableSharedFlow<Resource<Int>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(Resource.Empty)
        distinctUntilChanged()
    }

    private val sharedFlow: SharedFlow<Resource<Int>> = _sharedFlow

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        stateFlow_case()

        delim()

        sharedFlow_case()
    }

    private suspend fun stateFlow_case() = coroutineScope {
        val subscriber1 = launch {
            log("Subscriber1 subscribes")
            stateFlow.collect {
                log("Subscriber1: $it")
                delay(200)
            }
        }

        val subscriber2 = launch {
            delay(1_000)
            log("${spaces(8)}Subscriber2 subscribes after 1000ms")
            stateFlow.collect {
                log("${spaces(8)}Subscriber2: $it")
                delay(200)
            }
        }

        // Publisher
        launch {
            for (i in 0..5) {
                log("Emitting $i")
                _stateFlow.value = Resource.Success(i)
                delay(200)
            }
        }

        delay(3_000)
        subscriber1.cancelAndJoin()
        subscriber2.cancelAndJoin()
    }

    private suspend fun sharedFlow_case() = coroutineScope {
        val subscriber1 = launch {
            log("Subscriber1 subscribes")
            sharedFlow.collect {
                log("Subscriber1: $it")
                delay(200)
            }
        }

        val subscriber2 = launch {
            delay(1_000)
            log("${spaces(8)}Subscriber2 subscribes after 1000ms")
            sharedFlow.collect {
                log("${spaces(8)}Subscriber2: $it")
                delay(200)
            }
        }

        // Publisher
        launch {
            for (i in 0..5) {
                log("Emitting $i")
                _sharedFlow.emit(Resource.Success(i))
                delay(200)
            }
        }

        delay(3_000)
        subscriber1.cancelAndJoin()
        subscriber2.cancelAndJoin()
    }
}