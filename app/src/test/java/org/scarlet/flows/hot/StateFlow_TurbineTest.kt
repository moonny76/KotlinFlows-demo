package org.scarlet.flows.hot

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.scarlet.util.log
import org.scarlet.util.onCompletion
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
            repeat(4) {
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
            log("here1")
            emit(payload)
            log("here2, $currentTime")
            delay(1000) // what if to move this line before first emit?
            emit(payload + 1)
            log("here3, $currentTime")
        }.stateIn(scope = this)

        log("here4, $currentTime")

        launch {
            given.test {
                log("here5")
                log(awaitItem())
                log("here6")
                delay(1000)
                log("here7, $currentTime")
                log(awaitItem())
            }
        }
    }

    @Test
    fun `stateIn demo`() = runTest {
        val payload = 0
        val given: StateFlow<Int?> = flow {
            log("started ...")
            emit(payload)
            log("finished ...")
        }.stateIn(
            scope = this,
            started = SharingStarted.Lazily, // try Eagerly
//            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        ).apply {
            runCurrent()
        }

        log("here")

        given.test {
            log("in test")
            log(awaitItem())
            log(awaitItem())
        }

    }

}