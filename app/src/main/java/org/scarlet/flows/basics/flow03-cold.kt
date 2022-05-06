package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

/**
 * Flows are cold
 *
 * Flows are cold streams similar to sequences — the code inside a flow builder does not run
 * until the flow is collected.
 */

fun simple(): Flow<Int> = flow {
    log("Flow started")
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

object ColdFlow_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) {
        log("Calling simple function...")
        val flow = simple()
        log("Nothing happens...")
    }
}

object ColdFlow_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("Calling collect ...")
        val flow = simple()
        flow.collect { value -> log(value) }
    }
}

object ColdFlow_Demo3 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val flow = simple()

        log("Calling collect first time ...")
        flow.collect { value -> log(value) }

        log("Calling collect second time ...")
        flow.collect { value -> log(value) }
    }
}

object ColdFlow_Demo4 {
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
                log("Collector1")
                simple.collect { value -> log(value) }
            }

            launch {
                log("\t\t\tCollector2")
                simple.collect { value -> log("\t\t\t$value") }
            }
        }

        log("Done")
    }
}
