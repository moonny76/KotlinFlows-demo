package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Flow cancellation basics:
 *
 * Flow adheres to the general cooperative cancellation of coroutines. As usual, flow collection
 * can be cancelled when the flow is suspended in a cancellable suspending function (like delay).
 *
 * The following example shows how the flow gets cancelled on a timeout when running in a
 * `withTimeoutOrNull` block and stops executing its code:
 */

private fun conFlow(): Flow<Int> = flow {
    repeat(Int.MAX_VALUE) {
        println("Emitting $it")
        emit(it)
        delay(100)
    }
}

object Flow_Timeout1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        withTimeoutOrNull(500) { // Timeout after 500ms
            conFlow().collect { value -> println(value) }
        }
        println("Done")
    }

}

object Flow_Timeout2 {
    private fun slowFlow(): Flow<Int> = flow {
        delay(Long.MAX_VALUE) // very very long-running computation
        emit(42)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        withTimeout(500) { // Timeout after 500ms
            slowFlow().collect { value -> println(value) }
        }
        println("Done")
    }

}

object Explicit_Collector_Cancellation {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val collector = launch {
            conFlow().collect { value -> println(value) }
        }

        delay(1000)
        collector.cancelAndJoin()

        println("Done")

        delay(2000) // To check whether emitter is still alive...
    }

}

object Cancellation_when_Separate_Coroutines_Also_Works {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val collector = launch {
            conFlow().flowOn(Dispatchers.Default).collect { value -> println(value) }
        }

        delay(1000)
        collector.cancelAndJoin()

        println("Done")

        delay(2000)
    }
}