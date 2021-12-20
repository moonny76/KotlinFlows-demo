package org.scarlet.flows.advanced.composition

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Zip:
 *
 * Just like the Sequence.zip extension function in the Kotlin standard library,
 * flows have a `zip` operator that combines the corresponding values of two flows.
 * Shorter and slower flow determines when to zip, and terminate.
 */

object Zip_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val nums = (1..10).asFlow() // numbers 1..3
        val strs = flowOf("one", "two", "three") // strings

        nums.zip(strs) { a, b -> "$a -> $b" }
            .collect { println(it) } // collect and print
    }
}

object Zip_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val nums = (1..10).asFlow().map {
            delay(1000)
            it
        } // numbers 1..3

        val strs = flowOf("one", "two", "three")
            .map {
                delay(500)
                it
            }// strings

        nums.zip(strs) { a, b -> "$a -> $b" } // compose a single string
            .collect { println(it) } // collect and print
    }
}
