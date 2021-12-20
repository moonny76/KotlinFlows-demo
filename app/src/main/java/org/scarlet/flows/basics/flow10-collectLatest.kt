package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

/**
 * Processing the latest value:
 *
 * Conflation is one way to speed up processing when both the emitter and collector are slow.
 * It does it by dropping emitted values. The other way is to cancel a slow collector and restart
 * it every time a new value is emitted. There is a family of xxxLatest operators that perform
 * the same essential logic of a xxx operator, but cancel the code in their block on a new value.
 */

object CollectLatestDemo {

    fun simple(): Flow<Int> = flow {
        for (i in 1..2) {
            println("Emit: $i")
            emit(i) // emit next value
            delay(100) // pretend we are asynchronously waiting 100 ms
        }
    }

    //   emitter:      x        y
    // collector:        x       <cancel> y

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
                .collectLatest { value -> // cancel & restart on the latest value
                    try {
                        println("\tCollecting $value")
                        delay(150)  // pretend we are processing it for 150 ms
                        println("\tDone $value")
                    } catch(ex: Exception) {
                        println("Caught: ${ex.javaClass.simpleName}")
                    }
                }
        }
        println("Collected in $time ms")
    }
}
