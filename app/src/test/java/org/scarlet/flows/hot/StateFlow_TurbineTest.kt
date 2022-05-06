package org.scarlet.flows.hot

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import org.scarlet.util.coroutineInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.scarlet.util.log
import org.scarlet.util.onCompletion
import org.scarlet.util.spaces
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class StateFlow_TurbineTest {

    @Test
    fun `StateFlow test`() = runTest {
        val stateFlow = MutableStateFlow(0)

        launch {
            repeat(3) {
                stateFlow.value = it + 1
            }
        }

        stateFlow.test {
            repeat(3) {
                log("received value = ${awaitItem()}")
            }
            log(cancelAndConsumeRemainingEvents())
        }
    }

    @Test
    fun `stateFlow never completes but - turbine behavior`() = runTest {
        val stateFlow = MutableStateFlow(0)

        stateFlow
            .onCompletion { ex -> log("ON COMPLETE: ${ex?.javaClass?.name}") }
            .test {
                log(awaitItem())
            }
    }

    @Test
    fun `StateFlow - lost initial state`() = runTest {
        val hotFlow = MutableStateFlow(42)

        hotFlow.emit(1)

        hotFlow.test {
            val value = awaitItem()
            assertThat(value).isNotEqualTo(42)
            assertThat(value).isEqualTo(1)
        }
    }

    @Test
    fun `StateFlow - init value collected`() = runTest {
        val hotFlow = MutableStateFlow(42)

        val list = mutableListOf<Int>()
        hotFlow.test {
            hotFlow.emit(1)

            list.add(awaitItem())
            list.add(awaitItem())
            log(list)
        }
    }

    @Test
    fun `StateFlow - realistic test`() = runTest {
        // Arrange (Given)
        val gen = launch {
            genToken() // infinite flow
        }.onCompletion("Emitter")

        // Act (When)
        tokens.test {
            // Assert (Then)
            repeat(5) {
                log(awaitItem())
            }
        }
        gen.cancelAndJoin() // Must use this line.
    }

    @Test
    fun `suspending function version - stateIn`() = runTest {
        val payload = 0
        val given: StateFlow<Int> = flow {
            emit(payload)
            delay(1000) // what if to move this line one up?
            emit(payload + 1)
        }.stateIn(scope = this)

        launch {
            given.test {
                log(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

        launch {
            delay(1000)
            given.test {
                log(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `stateIn demo`() = runTest {
        val payload = 0
        val given: StateFlow<Int?> = flow {
            emit(payload)
            emit(payload + 1)
        }.stateIn(
            scope = this,
            started = SharingStarted.Eagerly, // same for Lazily
//            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

        given.test {
            log(awaitItem())
            log(awaitItem())
            log(awaitItem())
        }

//        coroutineContext.cancelChildren()
    }

}