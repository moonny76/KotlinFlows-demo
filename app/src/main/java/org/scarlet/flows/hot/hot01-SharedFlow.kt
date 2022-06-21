package org.scarlet.flows.hot

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.scarlet.util.*

/**
 * SharedFlow Demo
 *
 * SharedFlow is like a broadcast channel: everyone can send (emit) messages which will be
 * received by every coroutine that is listening (collecting).
 *
 * Shared flow waits for all subscribers to receive. Good fit for event handling.
 */

object SharedFlow_behaves_like_a_broadcast_channel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val mutableSharedFlow = MutableSharedFlow<String>(replay = 0)

        val subscriber1 = launch {
            mutableSharedFlow.collect {
                log("subscriber1: received $it")
            }
        }.onCompletion("Subscriber1")
        val subscriber2 = launch {
            mutableSharedFlow.collect {
                log("subscriber2: received $it")
            }
        }.onCompletion("Subscriber2")

        delay(100) // allow time for subscribers being ready

        launch {
            log("Emit Data1")
            mutableSharedFlow.emit("Data1")
            delay(2000)
            log("Emit Data2")
            mutableSharedFlow.emit("Data2")
        }.onCompletion("Publisher").join()

//        coroutineContext.job.cancelChildren()
//        joinAll(subscriber1, subscriber2)
    }
}

object SharedFlow_Single_Subscriber_Rendezvous {

    private val sharedFlow = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 0)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // Populate shared flow
        launch {
            for (i in 0..5) {
                log("Emitting $i (#subscribers = ${sharedFlow.subscriptionCount.value})")
                sharedFlow.emit(i) // emitted values are lost before the first subscriber
                log("Emitting $i done")
                delay(100)
            }
        }.onCompletion("Publisher")

        // Subscriber subscribes after 200ms later
        val subscriber = launch {
            delay(200)
            log("${spaces(7)}Subscribe to emitter")
            sharedFlow.collect {
                log("${spaces(7)}Subscriber: $it")
                delay(1000)
            }
        }.onCompletion("${spaces(7)}Subscriber")

        delay(5000)
        subscriber.cancelAndJoin()
    }
}

object SharedFlow_Single_Subscriber_with_Buffers {

    // Change extraBufferCapacity
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 0  // 0, 1, 2
    )

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        launch {
            for (i in 0..10) {
                log("Emitting $i (#subscribers = ${sharedFlow.subscriptionCount.value})")
                sharedFlow.emit(i)
                log("Emitting $i done")
                delay(50)
            }
        }

        // Slow collector
        val subscriber = launch {
            delay(150)
            log("${spaces(7)}Subscribe to sharedFlow")
            sharedFlow.collect {
                log("${spaces(7)}Subscriber: $it")
                delay(200)
            }
        }

        delay(3000)
        subscriber.cancelAndJoin()
    }
}

/**
 * MutableSharedFlow is conceptually similar to RxJava Subjects.
 *  - When replay = 0, similar to a PublishSubject.
 *  - When replay = 1, similar to a BehaviorSubject.<br>
 *  - When replay = Int.MAX_VALUE, similar to ReplaySubject.
 */
@ExperimentalCoroutinesApi
object SharedFlow_replayCache {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val mutableSharedFlow = MutableSharedFlow<String>(
            replay = 0, // 0, 1, 2
            onBufferOverflow = BufferOverflow.SUSPEND // Change to DROP_OLDEST
        )

        val subscriber = launch {
            mutableSharedFlow.collect {
                log("subscriber: received $it")
                delay(3000)
            }
        }

        launch {
            repeat(4) {
                log("publisher: emitting Data$it")
                mutableSharedFlow.emit("Data$it")
                log("publisher: emitting Data$it done")
                log("Replay cache = ${mutableSharedFlow.replayCache}")
                delim()
                delay(100)
            }
        }.join()

        delay(8000)
        mutableSharedFlow.resetReplayCache()
        log("After reset: cache = ${mutableSharedFlow.replayCache}")

        subscriber.cancelAndJoin()
    }
}

object SharedFlow_Multiple_Subscribers {

    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
        // DROP_OLDEST does not guarantee receivers accept all equal values.
    )

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // Populate shared flow
        launch {
            for (i in 0..10) {
                log("Emitting $i (#subscribers = ${sharedFlow.subscriptionCount.value})")
                sharedFlow.emit(i)
                log("Emitting $i done")
                delay(50)
            }
        }

        // Launch two collectors 300ms apart
        val subscriber1 = launch {
            log("${spaces(4)}Subscriber1: subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber1: $it")
                delay(100)
            }
        }

        val subscriber2 = launch {
            delay(300)
            log("${spaces(8)}Subscriber2: subscribes")
            sharedFlow.collect {
                log("${spaces(8)}Subscriber2: $it")
                delay(200)
            }
        }

        delay(3000)
        coroutineContext.job.cancelChildren()
        joinAll(subscriber1, subscriber2)
    }
}

/**
 * SharedFlow emits duplicated values.
 */
object SharedFlow_Emits_Duplicated_Values {

    private val sharedFlow = MutableSharedFlow<Resource<Int>>()

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val subscriber = launch {
            sharedFlow.collect {
                log("Subscriber: value = $it")
            }
        }

        // Populate sharedFlow
        launch {
            for (i in listOf(0, 1, 1, 2, 2, 3, 3)) {
                sharedFlow.emit(Resource.Success(i))
            }
        }.join()

        subscriber.cancelAndJoin()
    }
}

