package org.scarlet.flows.advanced.a1composition

/**
 * Combine:
 *
 * When flow represents the most recent value of a variable or operation,
 * it might be needed to perform a computation that depends on the most
 * recent values of the corresponding flows and to recompute it whenever
 * any of the upstream flows emit a value.
 * The corresponding family of operators is called `combine`.
 */
import org.scarlet.util.delim
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

// numbers 1..3 every 300 ms
val numbers = (1..3).asFlow().onEach { delay(300) }

// strings every 400 ms
val strings = flowOf("one", "two", "three").onEach { delay(400) }

object Flow_Zip_vs_Combine {
    @JvmStatic
    fun main(args: Array<String>) {
        funcZip()

        delim()

        funcCombine()
    }
}

fun funcZip() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    numbers.zip(strings) { a, b -> "$a -> $b" } // compose a single string with "zip"
            .collect { value ->
                log("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
}

fun funcCombine() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    numbers.combine(strings) { a, b -> "$a -> $b" } // compose a single string with "combine"
            .collect { value ->
                log("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
}