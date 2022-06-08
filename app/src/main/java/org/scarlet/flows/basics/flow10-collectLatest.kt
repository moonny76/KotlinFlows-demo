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
//        log("Emit: $i")
        emit(1)
        delay(30)
        emit(2)
        delay(30)
        emit(3)
        delay(100)
        emit(4)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val time = measureTimeMillis {
            simple()
                .collectLatest { value -> // cancel & restart on the latest value
                    currentCoroutineContext().job.onCompletion("collectLatest: value = $value")

                    delay(50)  // pretend we are processing it for 150 ms
                    log("\t$value collected")
                }
        }
        log("Collected in $time ms")
    }
}
