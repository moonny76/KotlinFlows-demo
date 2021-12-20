package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
        for (i in 1..10) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            println("Emitting $i at ${i * 100}")
            emit(i) // emit next value
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var timestamp = 0
        val time = measureTimeMillis {
            simple()
                .conflate() // conflate emissions, don't process each one
                .collect { value ->
                    println("\t\tCollector: $value at ${300 * timestamp++}")
                    delay(300) // pretend we are processing it for 300 ms
                }
        }
        println("Collected in $time ms")
    }
}
