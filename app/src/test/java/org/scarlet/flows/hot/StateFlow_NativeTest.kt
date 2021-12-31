package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class StateFlow_NativeTest {

    @Test
    fun `StateFlow test`() = runBlockingTest {
        val stateFlow = MutableStateFlow(0)

        launch {
            repeat(3) {
                println("emit time = $currentTime")
                stateFlow.value = it + 1
                delay(200) // change this 200, 1000
            }
        }

        val collector = launch {
            stateFlow.collect {
                println("\t\t\t\tcollect time = $currentTime")
                println("\t\t\t\tvalue = $it")
                delay(500)
            }
        }

        delay(2000)
        collector.cancelAndJoin()
    }

    @Test
    fun `stateFlow never completes`() = runBlockingTest {
        val stateFlow = MutableStateFlow(0)

        stateFlow
            .onCompletion { ex -> println("ON COMPLETE: ${ex?.javaClass?.name}") }
            .collect {
                println(it)
            }
    }

    @Test
    fun `StateFlow - lost initial state`() = runBlocking {
        val hotFlow = MutableStateFlow(42)

        hotFlow.emit(1)

        val value = hotFlow.first()
        assertThat(value).isNotEqualTo(42)
        assertThat(value).isEqualTo(1)
    }

    @Test
    fun `StateFlow - init value collected`() = runBlockingTest {
        val hotFlow = MutableStateFlow(42)

        val list = mutableListOf<Int>()
        val job = launch {
            hotFlow.collect {
                list.add(it)
            }
        }

        hotFlow.emit(1)

        delay(100)

        job.cancelAndJoin()
        assertThat(list).isEqualTo(listOf(42, 1))
    }

    @Test
    fun `StateFlow - realistic test`() = runBlockingTest {
        val gen = launch {
            genToken() // infinite flow
        }.apply { invokeOnCompletion { println("Emitter completes: ex = $it") } }

        val collector = launch {
            tokens.collect {
                println("collected value = $it")
            }
        }.apply {
            invokeOnCompletion {
                println("Collector completes: ex = $it")
//            gen.cancel()
            }
        }

        delay(1000)

        collector.cancel()
    }

    @Test
    fun `suspending function version - stateIn`() = runBlockingTest {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(scope = this)

        val subscriber1 = launch {
            assertThat(given.first()).isEqualTo(payload)
        }

        val subscriber2 = launch {
            assertThat(given.first()).isEqualTo(payload)
        }

        joinAll(subscriber1, subscriber2)
    }

    /**/

    @Test
    fun `stateIn - runBlockingTest`() = runBlockingTest {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = null
        )

        val result = mutableListOf<Int?>()
        val job = launch {
            given.take(2).toList(result)
        }

        delay(100)
        job.cancelAndJoin()

        assertThat(result).containsExactly(0)
    }

    @Test
    fun `stateIn - runBlocking`() = runBlocking<Unit> {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = null
        )

        val result = mutableListOf<Int?>()
        val job = launch {
            given.take(2).toList(result)
        }

        delay(100)
        job.cancelAndJoin()

        assertThat(result).containsExactly(null, 0)
    }

    @Test
    fun `stateIn - runBlockingTest - V2`() = runBlocking<Unit> {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

        val result = mutableListOf<Int?>()
        val job = launch {
            given.collect {
                result.add(it)
            }
        }

        delay(100)
        job.cancelAndJoin()

        assertThat(result).containsExactly(null, 0)
    }



}