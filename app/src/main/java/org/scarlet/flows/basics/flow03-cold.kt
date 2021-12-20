package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Flows are cold
 *
 * Flows are cold streams similar to sequences — the code inside a flow builder does not run
 * until the flow is collected.
 */

fun simple(): Flow<Int> = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

object ColdFlow_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Calling simple function...")
        val flow = simple()
        println("Nothing happens...")
    }
}

object ColdFlow_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("Calling collect ...")
        val flow = simple()
        flow.collect { value -> println(value) }
    }
}

object ColdFlow_Demo3 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val flow = simple()

        println("Calling collect first time ...")
        flow.collect { value -> println(value) }

        println("Calling collect second time ...")
        flow.collect { value -> println(value) }
    }
}

object ColdFlow_Demo４ {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val simple = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        coroutineScope {
            launch {
                println("Collector1")
                simple.collect { value -> println(value) }
            }

            launch {
                println("\t\t\tCollector2")
                simple.collect { value -> println("\t\t\t$value") }
            }
        }

        println("Done")
    }
}
