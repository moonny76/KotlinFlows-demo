package org.scarlet.flows.advanced.a1composition

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

/**
 * Zip:
 *
 * Just like the `Sequence.zip` extension function in the Kotlin standard library,
 * flows have a `zip` operator that combines the corresponding values of two flows.
 * Shorter and slower flow determines when to zip, and terminate.
 */

object Zip_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val numbers = (1..10).asFlow()
        val strings = flowOf("one", "two", "three")

        numbers.zip(strings) { a, b -> "$a -> $b" }
            .collect { log(it) }
    }
}

object Zip_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val numbers = (1..10).asFlow().map {
            delay(1000)
            it
        } // numbers 1..3

        val strings = flowOf("one", "two", "three")
            .map {
                delay(500)
                it
            }// strings

        numbers.zip(strings) { a, b -> "$a -> $b" }
            .collect { log(it) }
    }
}
