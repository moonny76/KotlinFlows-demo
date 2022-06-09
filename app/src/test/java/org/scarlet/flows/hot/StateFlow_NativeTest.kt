package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.scarlet.util.log
import org.scarlet.util.onCompletion

@ExperimentalCoroutinesApi
class StateFlow_NativeTest {

    @Test
    fun `StateFlow test`() = runTest {
        val stateFlow = MutableStateFlow(0)

        launch {
            repeat(3) {
                stateFlow.value = it + 1
                delay(1000) // change this 200, 1000
            }
        }.onCompletion("Emitter")

        val collector = launch {
            stateFlow.collect {
                log("received value = $it")
                delay(500)
            }
        }.onCompletion("Collector")

        delay(3000)
        collector.cancelAndJoin()
    }

    @Test
    fun `stateFlow never completes`() = runTest {
        val stateFlow = MutableStateFlow(0)

        stateFlow
            .onCompletion { ex -> log("ON COMPLETE: ${ex?.javaClass?.name}") }
            .collect {
                log("received = $it")
            }
    }

    @Test
    fun `StateFlow - lost initial state`() = runTest {
        val hotFlow = MutableStateFlow(42)

        hotFlow.emit(1)

        val value = hotFlow.first()
        assertThat(value).isNotEqualTo(42)
        assertThat(value).isEqualTo(1)
    }

    @Test
    fun `StateFlow - init value collected`() = runTest {
        val hotFlow = MutableStateFlow(42)

        val list = mutableListOf<Int>()
        val job = launch {
            hotFlow.collect {
                list.add(it)
            }
        }.also {
            runCurrent()
        }

        hotFlow.emit(1)

        delay(100)

        job.cancelAndJoin()
        assertThat(list).isEqualTo(listOf(42, 1))
    }

    @Test
    fun `StateFlow - realistic test`() = runBlocking {
        val emitter = launch {
            genToken() // infinite flow
        }.onCompletion("Emitter")

        val subscriber = launch {
            tokens.collect {
                log("received value = $it")
            }
        }.onCompletion("Subscriber")

        delay(1000)

        subscriber.cancelAndJoin()
        emitter.cancelAndJoin()
    }

    @Test
    fun `suspending function version of stateIn`() = runTest {
        val payload = 0
        val given: StateFlow<Int> = flow {
            log("started ...")
            emit(payload)
            delay(1000) // what if to move this line one up?
            emit(payload + 1)
            log("finished ...")
        }.stateIn(scope = this)

        launch {
            log(given.first())
            delay(1000)
            log(given.first())
        }
    }

    /**/

    @Test
    fun `stateIn - Early vs Lazily`() = runTest {
        val payload = 0
        val given: StateFlow<Int?> = flow {
            emit(payload)
        }.stateIn(
            scope = this,
            started = SharingStarted.Lazily, // What if using Early?
            initialValue = null
        ).apply {
            runCurrent()
        }

        val result = mutableListOf<Int?>()
        val job = launch {
            given.take(2).toList(result)
        }

        delay(1000)
        job.cancelAndJoin()

        assertThat(result).containsExactly(null, 0)
    }

    @Test
    fun `stateIn - WhileSubscribed`() = runTest {
        val payload = 0
        val given = flow {
            log(currentCoroutineContext())
            emit(payload)
        }.stateIn(
            scope = this, // Oops!
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

        val result = mutableListOf<Int?>()
        val job = launch {
            log("collector")
            given.take(2).toList(result)
        }.also {
            runCurrent()
        }

        job.cancelAndJoin()

        assertThat(result).containsExactly(null, 0)
    }

}