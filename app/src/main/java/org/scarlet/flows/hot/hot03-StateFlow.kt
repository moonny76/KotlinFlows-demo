package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

/**
 * ## StateFlow:
 *  - conflation
 *
 *  Not suitable for tracing locations (due to conflation) and event processing (due to uniqueness)
 *  Slow subscribers may miss intermediate values.
 */

object StateFlow_and_Conflation {

    // State flow must have an initial value.
    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    /**
     * Check conflation behavior using slow vs. fast collectors.
     */
    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        // Subscriber
        val subscriber = launch {
//            delay(450) // 1. Uncomment to check to see whether initial value collected or not
            log("${spaces(4)}Subscriber: subscribe to stateflow")
            stateFlow.collect {
                log("${spaces(4)}Subscriber: $it")
                delay(100) // 2. change to 100 (fast subscriber), 400 (slow subscriber)
            }
        }

        // Publisher
        launch {
            log("Publisher: started")
            for (i in 0..4) {
                log("Emit $i")
                stateFlow.value = Resource.Success(i)
                delay(200)
            }
        }

        delay(2_000)
        subscriber.cancelAndJoin()
    }
}

/**
 *  Check whether initial value delivered to late collector.
 */
object StateFlow_Late_Collector {

    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        // Publisher
        launch {
            for (i in 0..4) {
                stateFlow.value = Resource.Success(i)
                log("Emit $i (#subscribers = ${stateFlow.subscriptionCount.value})")
                delay(100)
            }
        }

        // A late subscriber
        val subscriber = launch {
            // To delay subscriber subscription
            delay(300)
            log("${spaces(4)}Subscriber: subscribe to stateflow")
            stateFlow.collect {
                log("${spaces(4)}subscriber: $it")
            }
        }

        delay(1_000)
        subscriber.cancelAndJoin()
    }
}

object StateFlow_Multiple_Subscribers {
    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        // Subscriber 1
        val subscriber1 = launch {
            log("${spaces(4)}Subscriber1 subscribes")
            stateFlow.collect {
                log("${spaces(4)}Subscriber1: $it")
                delay(100)
            }
        }

        // Subscriber 2
        val subscriber2 = launch {
            delay(250)
            log("${spaces(12)}Subscriber2 subscribes")
            stateFlow.collect {
                log("${spaces(12)}subscriber2: $it")
                delay(100) // Change 100, 400
            }
        }

        // Publisher
        launch {
            for (i in 0..4) {
                log("Emitter: $i")
                stateFlow.value = Resource.Success(i)
                delay(200)
            }
        }

        delay(2_000)
        coroutineContext.job.cancelChildren()
        joinAll(subscriber1, subscriber2)
    }
}

object StateFlow_Squash_Duplication {
    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        // Subscriber
        val subscriber = launch {
            log("${spaces(4)}Subscriber: subscribe to stateflow")
            stateFlow.collect {
                log("${spaces(4)}subscriber: $it")
            }
        }

        // Publisher
        launch {
            for (i in listOf(1, 1, 2, 2, 3, 3, 3)) {
                log("Emit $i")
                stateFlow.value = Resource.Success(i)
                delay(100)
            }
        }

        delay(1_000)
        subscriber.cancelAndJoin()
    }
}



