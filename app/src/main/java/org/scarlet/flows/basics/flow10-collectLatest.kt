package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import org.scarlet.util.onCompletion
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
//        log("Flow: coroutineContext = ${currentCoroutineContext()}")
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
        delay(500)
        emit(4)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
                .collectLatest { value -> // cancel & restart on the latest value
                    currentCoroutineContext().job.onCompletion("collectLatest: value = $value")
                    log("\t$value received")

                    delay(1000)  // pretend we are processing it for 100 ms
                    log("\t$value processing done")
                }
        }
        log("Collected in $time ms")
    }
}
