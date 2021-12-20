package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class StateFlow_NativeTest {

    @Test
    fun `stateFlow never completes`() = runBlocking {
        val stateFlow = MutableStateFlow(0)

        val job = launch {
            stateFlow
                .onCompletion { ex -> println("ON COMPLETE: ${ex?.javaClass?.name}") }
                .collect {
                    println(it)
                }
        }

        delay(100)

        // If not manually canceled, will hang forever
        job.cancelAndJoin()
    }

    @Test
    fun `StateFlow - realistic test`() = runBlocking {
        val gen = launch {
            genToken() // infinite flow
        }.apply { invokeOnCompletion { println("Emitter done: exception = $it") }}

        val collector = launch {
            tokens.collect {
                println("collected value = $it")
            }
        }.apply { invokeOnCompletion { println("Collector done: exception = $it") }}

        delay(500)

        collector.cancel()
        gen.cancel()
    }

    /**
     * The root cause is that stateIn does not always subscribe to the upstream flow when its downstream subscriber gets initial value and cancels too fast.
     */

    // What's the problem?
    @Test
    fun testEmission_What_is_Wrong() = runBlocking {
        val payload = 0
        val given = flow {
//            println("here i am")
//            delay(1) // Need delay to prevent conflation
            emit(payload)
//            println("emitted $payload")
        }
//            .onEach { delay(1) }
            .stateIn(
            scope = this,
            // Only Eagerly works! - deadlock?
//            started = SharingStarted.Eagerly,
            started = SharingStarted.WhileSubscribed(),
//            started = SharingStarted.Lazily,
            initialValue = null
        )

        println("subscribe ...")
        val flowOutputs = given.take(2).toList()

        assertThat(flowOutputs[0]).isNull()
        assertThat(flowOutputs[1]).isEqualTo(payload)
    }

    @Test
    fun testEmission_Solution() = runBlocking {
        val payload = 0
        val given = flow {
            delay(200)
            emit(payload)
        }.stateIn(
            scope = this + Job(), // GlobalScope also works
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

        val flowOutputs: MutableList<Int?> = mutableListOf()

        delay(100)

        val collector = launch {
            given.take(2).toList(flowOutputs)
        }

        delay(1000)

        assertThat(flowOutputs.get(0)).isNull()
        assertThat(flowOutputs.get(1)).isEqualTo(payload)

        collector.cancelAndJoin()
    }

    // What's the problem?
    @Test
    fun testEmission4() = runBlockingTest {
        val payload = 0
        val given = flow {
            delay(120) // need this to allow time gap
            emit(payload)
        }.stateIn(
            scope = this + Job(),
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

        val job = launch {
            given.collect {
                println("value = $it")  // How to assert two consecutive values?
            }
        }

        delay(1000)
        job.cancelAndJoin()
    }


}