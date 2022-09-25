package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.delim
import org.scarlet.util.log

/**
 * flowOn operator:
 *
 * The exception refers to the `flowOn` function that shall be used to change the context of the flow emission.
 * Notice how flow { ... } works in the background thread, while collection happens in the main thread.
 *
 * Another thing to observe here is that the `flowOn` operator has changed the default sequential nature
 * of the flow. Now collection happens in one coroutine ("coroutine#1") and emission happens in another
 * coroutine ("coroutine#2") that is running in another thread concurrently with the collecting coroutine.
 *
 * The `flowOn` operator creates another coroutine for an upstream flow when it has to change the
 * CoroutineDispatcher in its context.
 */

object FlowOn_Demo {

    private fun simple() = flow {
        log("Flow: ${currentCoroutineContext()}")

        delay(100) // pretend we are computing it in CPU-consuming way
        log("Emitting 42")
        emit(42)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        simple()
//            .flowOn(Dispatchers.Default)
            .collect { value ->
                log("Collector: ${currentCoroutineContext()}")
                log("Collected $value")
            }
    }
}

/**
 * Channel Flow
 */
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
object ChannelFlow_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        myFlow().collect { value ->
            log("Collector: ${currentCoroutineContext()}")
            log(value)
        }
    }

    private fun myFlow() = channelFlow {
        log("channelFlow context = ${currentCoroutineContext()}")

        launch(CoroutineName("Child1") + Dispatchers.Default) {
            log("Child1: $coroutineContext")
            delay(500)
            send(42)
        }

        delim()

        launch(CoroutineName("Child2")) {
            log("Child2: $coroutineContext")
            delay(500)
            send(1569)
        }

        delim()

        withContext(newSingleThreadContext("myThread")) {
            log("WithContext: $coroutineContext")
            delay(1000)
            send(777)
        }

    }

}


