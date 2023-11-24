package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import kotlin.system.*

/**
 * Conflation:
 *
 * When a flow represents partial results of the operation or operation status updates,
 * it may not be necessary to process each value, but instead, only most recent ones.
 * In this case, the `conflate` operator can be used to skip intermediate values when a
 * collector is too slow to process them.
 */

object ConflationDemo {

    fun simple(): Flow<Int> = flow {
        log("Flow: ${currentCoroutineContext()}")

        for (i in 1..10) {
            log("Emitting $i")
            emit(i)
            delay(100)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
//                .conflate() // conflate emissions, don't process each one
                .collect { value ->
                    if (value == 1) {
                        log("Collector: ${currentCoroutineContext()}")
                    }
                    log("\t\tCollector: $value")
                    delay(300) // pretend we are processing it for 300 ms
                }
        }
        log("Collected in $time ms")
    }
}
