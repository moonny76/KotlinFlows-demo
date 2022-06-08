package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import org.scarlet.util.onCompletion

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
        try {
            log("Emitting $it")
            emit(it)
            delay(1000)
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                log("Flow cancelled")
                throw ex
            }
        }
    }
}

object Flow_Timeout1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        withTimeoutOrNull(3000) {
            coroutineContext.job.invokeOnCompletion { ex ->
                log("Collector completed: isCancelled = ${coroutineContext.job.isCancelled}, ex = $ex")
            }
            conFlow().collect { value -> log(value) }
        }

        log("Done")
    }

}

object Flow_Timeout2 {
    private fun slowFlow(): Flow<Int> = flow {
        try {
            delay(Long.MAX_VALUE) // very very long-running computation
            emit(42)
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                log("Flow cancelled")
                throw ex
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        try {
            withTimeout(500) { // Timeout after 500ms
                slowFlow().collect { value -> log(value) }
            }
        } catch (ex: Exception) {
            log(ex.javaClass.simpleName)
        } finally {
            log("Done")
        }
    }

}

object Explicit_Collector_Cancellation {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val collector = launch {
            conFlow().collect { value -> log(value) }
        }.onCompletion("collector")

        delay(3000)
        collector.cancelAndJoin()

        log("Done")
    }

}

object Cancellation_when_Separate_Coroutines_Also_Works {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val collector = launch {
            conFlow().flowOn(Dispatchers.Default).collect { value -> log(value) }
        }.onCompletion("Collector")

        delay(3000)
        collector.cancelAndJoin()

        log("Done")
    }
}