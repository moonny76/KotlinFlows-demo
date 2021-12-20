package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * StateFlow:
 *  - conflation
 *
 *  Not suitable for tracing locations (due to conflation) and event processing (due to uniqueness)
 *  Slow subscribers may miss intermediate values.
 */

object StateFlow_Hot_and_Conflation {

    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    /**
     * Check conflation behavior using slow vs. fast collectors.
     */
    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val collector = launch {
//            delay(450) // 2. Uncomment to check to see whether initial value collected or not
            println("${spaces(4)}Collector: Subscribe to stateflow")
            stateFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100) // 1. change to 100 (fast collector), 200, 400 (slow collector)
            }
        }

        // Populate state flow
        launch {
            for (i in 0..5) {
                println("Emit $i")
                stateFlow.value = Resource.Success(i)
                delay(200)
            }
        }

        delay(2000)
        collector.cancelAndJoin()
    }
}

/**
 *  Check whether initial value delivered to late collector.
 */
object StateFlow_Hot_and_Late_Collector {

    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        // Populate state flow
        launch {
            for (i in 0..5) {
                println("Emit $i")
                stateFlow.value = Resource.Success(i)
                delay(100)
            }
        }

        // To delay collector subscription
        delay(400)

        val collector = launch {
            println("${spaces(4)}Collector: Subscribe to stateflow")
            stateFlow.collect {
                println("${spaces(4)}collector: $it")
            }
        }

        delay(1000)
        collector.cancelAndJoin()
    }
}

object StateFlow_Squash_Duplication {
    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        val collector = launch {
            println("${spaces(4)}Collector: Subscribe to stateflow")
            stateFlow.collect {
                println("${spaces(4)}collector: $it")
            }
        }

        // Populate state flow
        launch {
            for (i in listOf(1,1,2,2,3,3,3)) {
                println("Emit $i")
                stateFlow.value = Resource.Success(i)
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()
    }
}

object StateFlow_Two_Subscribers {
    private val stateFlow = MutableStateFlow<Resource<Int>>(Resource.Empty)

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val collector1 = launch {
            println("${spaces(4)}Collector1 subscribes")
            stateFlow.collect {
                println("${spaces(4)}Collector1: $it")
                delay(100)
            }
        }

        val collector2 = launch {
            delay(250)
            println("${spaces(12)}Collector2 subscribes")
            stateFlow.collect {
                println("${spaces(12)}collector2: $it")
                delay(400) // Change 100, 400
            }
        }

        // Populate stateflow
        launch {
            for (i in 0..5) {
                println("Emitter: $i")
                stateFlow.value = Resource.Success(i)
                delay(200)
            }
        }

        delay(2000)
        collector1.cancel()
        collector2.cancel()
    }
}

object StateFlow_Three_Subscribers {
    private val _stateFlow = MutableStateFlow(-1)
    val stateFlow = _stateFlow.asStateFlow()

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val generator = launch {
            println("Emitter started")
            repeat(Int.MAX_VALUE) {
                println("Emitting: $it")
                _stateFlow.value = it
                delay(500)
            }
        }

        val collector1 = launch {
            delay(100)
            println("${spaces(4)}Collector1 subscribes ...")
            stateFlow.collect {
                println("${spaces(4)}Collector1: $it")
                delay(200)
            }
        }

        val collector2 = launch {
            delay(500)
            println("${spaces(8)}Collector2 subscribes ...")
            stateFlow.collect {
                println("${spaces(8)}Collector2: $it")
                delay(500)
            }
        }

        delay(1000)

        val collector3 = launch {
            println("${spaces(12)}Collector3 subscribes ...")
            stateFlow.collect {
                println("${spaces(12)}Collector3: $it")
                delay(1000)
            }
        }

        delay(10_000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
        collector3.cancelAndJoin()
        generator.cancelAndJoin()
    }
}




