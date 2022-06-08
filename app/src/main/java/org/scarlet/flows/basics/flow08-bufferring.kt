package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import kotlin.system.*

/**
 * Buffering
 */

object Buffering {
    fun simple(): Flow<Int> = flow {
        log(currentCoroutineContext())
        for (i in 1..3) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            emit(i)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
//                .buffer()
                .collect { value ->
                    log(value)
                    log(currentCoroutineContext())
                    delay(100) // pretend we are processing it for 300 ms
                }
        }
        log("Collected in $time ms")
    }
}
