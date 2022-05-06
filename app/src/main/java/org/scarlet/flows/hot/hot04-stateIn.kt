package org.scarlet.flows.hot

import org.scarlet.util.Resource
import org.scarlet.util.delim
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
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
            log("Emitter: $it")
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

        val subscriber1 = launch {
            stateFlow.collect { value ->
                log("${spaces(4)}Subscriber1: $value")
            }
        }

        // Subscribe 500ms later
        val subscriber2 = launch {
            delay(500)
            stateFlow.collect { value ->
                log("${spaces(8)}Subscriber2: $value")
            }
        }

        delay(2000)
        subscriber1.cancelAndJoin()
        subscriber2.cancelAndJoin()
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
        emit("Hello $seed")
        log("Hello")
        emit("Hola $seed")
        log("Hola")
    }.onEach { delay(3000) }

    private suspend fun coldFlowDemo() {
        greetingFlow.collect { greeting -> log("1: $greeting") }
        greetingFlow.collect { greeting -> log("2: $greeting") }
    }

    private suspend fun hotFlowDemo() = coroutineScope {
        // Note that this `stateIn` is a suspending function
        val greetingState = greetingFlow.stateIn(this)

        val subscriber1 = launch {
            log("Subscriber1 launched")
            greetingState.collect { greeting -> log("1: $greeting") }
        }
        val subscriber2 = launch {
            log("Subscriber2 launched")
            greetingState.collect { greeting -> log("2: $greeting") }
        }

        delay(5000)
        subscriber1.cancelAndJoin()
        subscriber2.cancelAndJoin()
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        coldFlowDemo()

        delim()

        hotFlowDemo()
    }
}

object stateIn_ColdToHot_Eagerly_vs_Lazily {

    val coldFlow: Flow<Resource<Int>> = flow {
        for (i in 0..5) {
            log("Emit: $i")
            emit(Resource.Success(i))
            delay(1000)
        }
    }

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        val stateFlow: StateFlow<Resource<Int>> = coldFlow.stateIn(
            this,
            SharingStarted.Eagerly, // Lazily
            Resource.Empty
        ).apply { log("Sharing starts ...") }

        val subscriber1 = launch {
            delay(1000) // start after stateflow initialize
            log("${spaces(4)}Subscriber1 subscribes ...")
            stateFlow.collect {
                log("${spaces(4)}Subscriber1: $it")
                delay(1500)
            }
        }

        val subscriber2 = launch {
            delay(3500)
            log("${spaces(4)}Subscriber2 subscribes ...")
            stateFlow.collect {
                log("${spaces(4)}Subscriber2: $it")
            }
        }

        delay(7000)
        coroutineContext.job.cancelChildren()
        joinAll(subscriber1, subscriber2)
    }
}

// Note that program doesn't terminate.
@DelicateCoroutinesApi
object WhileSubscribed_Demo {

    val coldFlow: Flow<Int> = flow {
        repeat(10) {
            log("Emit: $it")
            emit(it)
            delay(500)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val stateFlow: StateFlow<Int?> = coldFlow.stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 1000),
            initialValue = null
        )

        // First subscriber
        val subscriber1 = launch {
            delay(3000)
            log("${spaces(4)}Subscriber1 subscribes ...")
            stateFlow.collect { value ->
                log("${spaces(4)}Subscriber1: $value")
            }
        }

        delay(5000)
        subscriber1.cancelAndJoin()
        log("${spaces(4)}Subscriber1 cancelled")

        delim()

        // Second subscriber joined later
        delay(500) // Change 500, 1500 to see the effect of `replayExpirationMillis`
        val subscriber2 = launch {
            log("${spaces(4)}Subscriber2 subscribes ...")
            stateFlow.collect { value ->
                log("${spaces(4)}Subscriber2: $value")
            }
        }

        delay(5000)
        subscriber2.cancelAndJoin()

        log(coroutineContext.job.children.toList())
        coroutineContext.job.cancelChildren()
    }
}

