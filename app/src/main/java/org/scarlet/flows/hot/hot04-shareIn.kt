package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * sharedIn Demo
 */

object sharedIn_Eager {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                println("Emitting $i")
                emit(Resource.Success(i))
                println("Emitting $i done")
                delay(50)
            }
        }

        val sharedFlow: SharedFlow<Resource<Int>> = coldFlow.shareIn(
            scope = this,
            started = SharingStarted.Eagerly,
            replay = 0
        )

        val collector = launch {
            delay(100) // subscribes after delay
            println("${spaces(4)}Collector subscribes")
            sharedFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()
    }
}

object shareIn_Lazy {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                println("Emitting $i")
                emit(Resource.Success(i))
                println("Emitting $i done")
                delay(20)
            }
        }

        val sharedFlow: SharedFlow<Resource<Int>> = coldFlow.shareIn(
            scope = this,
            started = SharingStarted.Lazily,
            replay = 0
        )

        val collector = launch {
            delay(100) // subscribes after delay
            println("${spaces(4)}Collector subscribes")
            sharedFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()
    }

}
