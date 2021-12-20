package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

/**
 * SharedFlow Demo
 *
 * Shared flow waits for all subscribers to receive. Good fit for event handling.
 */

/**
 * SharedFlow emits duplicated values.
 */
object SharedFlow_Emits_Duplicated_Values {

    private val sharedFlow = MutableSharedFlow<Resource<Int>>(0)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val collector = launch {
            sharedFlow.collect {
                println("Collector: value = $it")
            }
        }

        // Populate sharedflow
        launch {
            for (i in listOf(0,1,1,2,2,3,3)) {
                sharedFlow.emit(Resource.Success(i))
            }
        }

        delay(100)
        collector.cancelAndJoin()
    }
}

object SharedFlow_Single_Subscriber_Rendezvous {

    private val sharedFlow = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 0)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // Populate shared flow
        launch {
            for (i in 0..5) {
                println("Emitting $i")
                sharedFlow.emit(i)
                println("Emitting $i done")
                delay(200)
            }
        }.apply { invokeOnCompletion { println("Emitter completed: ${it?.javaClass?.simpleName}") } }

        delay(500) // To start collector later

        // Collector subscribes after 500ms later
        val collector = launch {
            println("${spaces(7)}Subscribe to emitter")
            sharedFlow.collect {
                println("${spaces(7)}Collector: $it")
                delay(100)
            }
        }.apply { invokeOnCompletion { println("${spaces(7)}Collector completed: ${it?.javaClass?.simpleName}") } }

        delay(1000)
        collector.cancelAndJoin()
    }
}

object SharedFlow_Single_Subscriber_with_Buffers {

    // Change buffer sizes
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND // Change to DROP_OLDEST
    )

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // Populate shared flow
        launch {
            for (i in 0..10) {
                println("# Subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emitting $i")
                sharedFlow.emit(i)
                println("Emitting $i done")
                delay(50)
            }
        }

        // Slow collector
        val collector = launch {
            delay(150)
            println("${spaces(7)}Subscribe to sharedFlow")
            sharedFlow.collect {
                println("${spaces(7)}Collector: $it")
                delay(200)
            }
        }

        delay(5000)
        collector.cancelAndJoin()
    }
}

object SharedFlow_Multiple_Subscribers {

    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
//        onBufferOverflow = BufferOverflow.SUSPEND
        // DROP_OLDEST does not guarantee receivers accept all equal values.
        // SO discouraged for multiple subscribers. (Kim's suggestion)
    )

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // Populate shared flow
        launch {
            for (i in 0..10) {
                println("# Subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emitting $i")
                sharedFlow.emit(i)
                println("Emitting $i done")
                delay(50)
            }
        }

        // Launch two collectors 300ms apart
        val collector1 = launch {
            println("${spaces(4)}Collector1: Subscribes")
            sharedFlow.collect {
                println("${spaces(4)}Collector1: $it")
                delay(100)
            }
        }

        val collector2 = launch {
            delay(300)
            println("${spaces(8)}Collector2: Subscribes")
            sharedFlow.collect {
                println("${spaces(8)}Collector2: $it")
                delay(200)
            }
        }

        delay(3000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }
}
