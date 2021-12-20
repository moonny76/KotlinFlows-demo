package org.scarlet.flows.advanced.context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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

    private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

    private fun simple() = flow {
        println(currentCoroutineContext()[CoroutineDispatcher.Key])

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
            println(value)
        }
    }

    private fun myFlow() = channelFlow {
        launch(Dispatchers.Default) {
            for (i in 1..3) {
                delay(500) // pretend we are computing it in CPU-consuming way
                send(i) // emit next value
            }
        }

        launch {
            for (i in 10..12) {
                delay(500) // pretend we are computing it in CPU-consuming way
                send(i) // emit next value
            }
        }
    }

}


