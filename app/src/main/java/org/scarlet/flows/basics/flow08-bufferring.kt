package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

/**
 * Buffering
 */

object Buffering {
    fun simple(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            emit(i)
//            println(currentCoroutineContext())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
//                .buffer()
                .collect { value ->
                    println(value)
                    delay(100) // pretend we are processing it for 300 ms
//                    println(coroutineContext)
                }
        }
        println("Collected in $time ms")
    }
}
