package org.scarlet.flows.hot

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.*

/**
 * Convert cold flow to hot flow.
 */

/**
 * sharedIn Demo
 */

object sharedIn_Demo {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Int> = flow {
            for (i in 0..5) {
                emit(i)
                delim()
                log("Emitting $i done")
                delay(1000)
            }
        }

        val sharedFlow: SharedFlow<Int> = coldFlow.shareIn(
            scope = this,
            started = SharingStarted.Eagerly, // Eagerly, Lazily, WhileSubscribed
            replay = 0 // default
        )

        delay(1500) // first subscriber joins 1500ms later

        val subscriber1 = launch {
            log("${spaces(4)}Subscriber1 subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber 1: $it")
            }
        }.onCompletion("Subscriber1")

        delay(1000) // second subscriber joins 2500ms later

        val subscriber2 = launch {
            log("${spaces(8)}Subscriber2 subscribes")
            sharedFlow.collect {
                log("${spaces(8)}Subscriber 2: $it")
            }
        }.onCompletion("Subscriber2")

        delay(1000) // third subscriber joins 3500ms later

        val subscriber3 = launch {
            log("${spaces(12)}Subscriber3 subscribes")
            sharedFlow.collect {
                log("${spaces(12)}Subscriber 3: $it")
            }
        }.onCompletion("Subscriber3")

        delay(2000)
        coroutineContext.job.cancelChildren()
        joinAll(subscriber1, subscriber2, subscriber3)
    }
}

object sharedIn_Eager {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Int> = flow {
            for (i in 0..5) {
                emit(i)
                delim()
                log("Emitting $i done")
                delay(50)
            }
        }

        val sharedFlow: SharedFlow<Int> = coldFlow.shareIn(
            scope = this,
            /**
             * Sharing is started immediately and never stops.
             */
            started = SharingStarted.Eagerly,
            replay = 0
        )

        val subscriber = launch {
            delay(150) // subscribes after delay
            log("${spaces(4)}Subscriber subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber: $it")
                delay(100)
            }
        }

        delay(1000)
        subscriber.cancelAndJoin()
    }
}

object shareIn_Lazy {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Int> = flow {
            for (i in 0..5) {
                emit(i)
                delim()
                log("Emitting $i done")
                delay(20)
            }
        }

        val sharedFlow: SharedFlow<Int> = coldFlow.shareIn(
            scope = this,
            /**
             * Sharing is started when the first subscriber appears and never stops.
             */
            started = SharingStarted.Lazily,
            replay = 0
        )

        val collector = launch {
            delay(150) // subscribes after delay
            log("${spaces(4)}Subscriber subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber: $it")
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()
    }

}

object SharedFlow_WhileSubscribed {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val flow = flowOf("A", "B", "C", "D")
            .onStart { log("Flow started") }
            .onEach { delay(1000) }
            .onCompletion { log("Flow finished") }

        val sharedFlow = flow.shareIn(
            scope = this,
            /**
             * Sharing is started when the first subscriber appears, immediately stops when the last
             * subscriber disappears (by default), keeping the replay cache forever (by default).
             */
            started = SharingStarted.WhileSubscribed(),
            replay = 0 // 1
        )

        delay(3000)

        val subscriber1 = launch {
            log("${spaces(4)}Subscriber1 subscribes")
            sharedFlow.collect {
                log("${spaces(4)}subscriber1: $it")
            }
        }.onCompletion("${spaces(4)}Subscriber1 leaves")

        launch {
            delay(2000)
            log("${spaces(8)}Subscriber2 subscribes")
            log("${spaces(8)}subscriber2: ${sharedFlow.take(2).toList()}")
        }.onCompletion("${spaces(8)}Subscriber2 leaves")

        delay(3000)
        subscriber1.cancelAndJoin()

        launch {
            delay(1000)
            log("${spaces(12)}Subscriber3 subscribes")
            log("${spaces(12)}subscriber3: ${sharedFlow.first()}")
        }.onCompletion("${spaces(12)}Subscriber3 leaves")

        delay(5000)
        coroutineContext.job.cancelChildren()
    }
}
