package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.delim
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

/**
 * stateIn Demo
 *
 * The stateIn operator is useful in situations when there is a cold flow that provides
 * updates to the value of some state and is expensive to create and/or to maintain, but
 * there are multiple subscribers that need to collect the most recent state value.
 */

@DelicateCoroutinesApi
object stateIn_Demo {

    // Cold flow
    private val countingFlow: Flow<Int> = flow {
        repeat(10) {
            println("Emitter: $it")
            emit(it)
            delay(100)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val stateFlow: StateFlow<Int?> = countingFlow.stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = null
        )

        val collector1 = launch {
            stateFlow.collect { value ->
                println("${spaces(4)}Collector1: $value")
            }
        }

        // Subscribe 500ms later
        val collector2 = launch {
            delay(500)
            stateFlow.collect { value ->
                println("${spaces(8)}Collector2: $value")
            }
        }

        delay(2000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }
}

/**
 * Suspending function `stateIn`
 * https://github.com/Kotlin/kotlinx.coroutines/issues/2047
 *
 * suspend fun <T> Flow<T>.stateIn(scope: CoroutineScope): StateFlow<T>
 *
 * -- Always need a value, so wait until the first value (i.e., initial value) is available.
 * When execution happens in suspending context and you want to compute and wait for the
 * initial value of the state to arrive from the upstream flow, there is a suspending
 * variant of stateIn without initial value and with the hard-coded sharingStarted = Eagerly
 */

object Suspendingfunction_StateIn {
    private val greetingFlow = flow {
        val seed = Random.nextInt()
        println("Emit Hello")
        emit("Hello $seed")
        delay(300)
        println("Emit Hola")
        emit("Hola $seed")
    }

    private suspend fun coldFlowDemo() {
        greetingFlow.collect { greeting -> println("1: $greeting") }
        greetingFlow.collect { greeting -> println("2: $greeting") }
    }

    private suspend fun hotFlowDemo() = coroutineScope {
        // Note that this `stateIn` is a suspending function
        val greetingState = greetingFlow.stateIn(this)

        val collector1 = launch {
            println("Collector1 launched")
            greetingState.collect { greeting -> println("1: $greeting") }
        }
        val collector2 = launch {
            println("Collector2 launched")
            greetingState.collect { greeting -> println("2: $greeting") }
        }

        delay(2000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        coldFlowDemo()
        delim()

        hotFlowDemo()
    }
}

object stateIn_ColdToHot_Eager {

    val coldFlow: Flow<Resource<Int>> = flow {
        for (i in 0..5) {
            println("Emit: $i")
            emit(Resource.Success(i))
            delay(50)
        }
    }

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val stateFlow: StateFlow<Resource<Int>> = coldFlow.stateIn(
            this,
            SharingStarted.Eagerly,
            Resource.Empty
        ).apply { println("Sharing starts eagerly") }

        val collector = launch {
            delay(100) // start after stateflow initialize
            println("${spaces(4)}Collector subscribes ...")
            stateFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()

        println("${coroutineContext[Job]?.children?.toList()}")

        val collector2 = launch {
            delay(100) // start after stateflow initialize
            println("${spaces(4)}Collector subscribes ...")
            stateFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100)
            }
        }

        println("${coroutineContext[Job]?.children?.toList()}")

        delay(1000)
        collector2.cancelAndJoin()

        println("${coroutineContext[Job]?.children?.toList()}")
    }
}

object stateIn_ColdToHot_Lazy {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        coroutineContext[Job]?.invokeOnCompletion {
            println("Scope completed with $it")
        }

        val coldFlow: Flow<Resource<Int>> = flow {
            for (i in 0..5) {
                println("Emit: $i")
                emit(Resource.Success(i))
                delay(50)
            }
        }

        val stateFlow: StateFlow<Resource<Int>> = coldFlow.stateIn(
            this,
            SharingStarted.Lazily,
            Resource.Empty
        ).apply { println("Sharing will start lazily") }

        val collector = launch {
            delay(500) // start after stateflow initialize
            println("${spaces(4)}Collector subscribes ...")
            stateFlow.collect {
                println("${spaces(4)}Collector: $it")
                delay(100)
            }
        }

        delay(1000)
        collector.cancelAndJoin()

        println("${coroutineContext[Job]?.children?.toList()}")
    }
}

// Note that program doesn't terminate.
@DelicateCoroutinesApi
object WhileSubscribed_Demo {

    private val flow = flow {
        emit(Random.nextInt())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        val stateFlow: StateFlow<Int?> = flow.stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 1000),
            initialValue = null
        )

        // First subscriber
        val collector1 = launch {
            stateFlow.collect { value ->
                println("Collector1: $value")
            }
        }

        delay(200) // allow time generate random number
        collector1.cancelAndJoin()

        delim()

        // Second subscriber joined later
        delay(500) // Change 500, 1500 to see the effect of `replayExpirationMillis`
        val collector2 = launch {
            stateFlow.collect { value ->
                println("Collector2: $value")
            }
        }

        delay(100)
        collector2.cancelAndJoin()

        println("${coroutineContext[Job]?.children?.toList()}")
        coroutineContext[Job]?.cancel()
    }
}

