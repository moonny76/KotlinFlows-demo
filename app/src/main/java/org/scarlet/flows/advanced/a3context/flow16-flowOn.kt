package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

@ExperimentalStdlibApi
object FlowOn_Demo {

    private fun simple() = flow {
        log(currentCoroutineContext()[CoroutineDispatcher.Key])

        for (i in 1..3) {
            delay(100) // pretend we are computing it in CPU-consuming way
            log("Emitting $i")
            emit(i)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        simple()
            .flowOn(Dispatchers.Default)
            .collect { value ->
                log("Collected $value")
            }
    }
}

/**
 * Channel Flow
 */
@ExperimentalCoroutinesApi
object ChannelFlow_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        myFlow().collect { value ->
            log(value)
        }
    }

    private fun myFlow() = channelFlow {
        log("channelFlow context = ${currentCoroutineContext()}")

        launch(CoroutineName("Child1") + Dispatchers.Default) {
            log("${currentCoroutineContext()}")
            for (i in 1..3) {
                delay(500) // pretend we are computing it in CPU-consuming way
                send(i) // emit next value
            }
        }

        launch(CoroutineName("Child2")) {
            log("${currentCoroutineContext()}")
            for (i in 10..12) {
                delay(500) // pretend we are computing it in CPU-consuming way
                send(i) // emit next value
            }
        }
    }

}


