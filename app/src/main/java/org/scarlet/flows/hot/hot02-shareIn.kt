package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import org.scarlet.util.onCompletion

/**
 * sharedIn Demo
 */

object sharedIn_Demo {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                emit(Resource.Success(i))
                log("Emitting $i done")
                delay(1000)
            }
        }

        val sharedFlow: SharedFlow<Resource<Int>> = coldFlow.shareIn(
            scope = this,
            started = SharingStarted.Eagerly,
            replay = 0
        )

        delay(1500)

        val subscriber1 = launch {
            log("${spaces(4)}Subscriber1 subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber 1: $it")
            }
        }.onCompletion("Subscriber1")

        delay(1000)

        val subscriber2 = launch {
            log("${spaces(4)}Subscriber2 subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber 2: $it")
            }
        }.onCompletion("Subscriber2")

        delay(1000)

        val subscriber3 = launch {
            log("${spaces(4)}Subscriber3 subscribes")
            sharedFlow.collect {
                log("${spaces(4)}Subscriber 3: $it")
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
        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                emit(Resource.Success(i))
                log("Emitting $i done")
                delay(50)
            }
        }

        val sharedFlow: SharedFlow<Resource<Int>> = coldFlow.shareIn(
            scope = this,
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
        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                emit(Resource.Success(i))
                log("Emitting $i done")
                delay(20)
            }
        }

        val sharedFlow: SharedFlow<Resource<Int>> = coldFlow.shareIn(
            scope = this,
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
    fun main(args: Array<String>) = runBlocking<Unit> {
        val flow = flowOf("A", "B", "C", "D")
            .onStart { log("Flow started") }
            .onCompletion { log("Flow finished") }
            .onEach { delay(1000) }

        val sharedFlow = flow.shareIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(),
        )

        delay(3000)

        val subscriber1 = launch {
            log("subscriber1 join: ${sharedFlow.first()}")
        }.onCompletion("Subscriber1")
        val subscriber2 = launch {
            log("subscriber2 join: ${sharedFlow.take(2).toList()}")
        }.onCompletion("Subscriber2")

        delay(3000)
        val subscriber3 = launch {
            log("subscriber3 join: ${sharedFlow.first()}")
        }.onCompletion("Subscriber3")

        delay(3000)
        coroutineContext.job.cancelChildren()
    }
}
