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
import org.junit.Test
import org.scarlet.util.testDispatcher
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class StateFlow_TurbineTest {

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

        stateFlow.test {
            repeat(3) {
                println("\t\t\t\tcollect time = $currentTime")
                println("\t\t\t\tvalue = ${awaitItem()}")
                delay(500)
            }
            println(cancelAndConsumeRemainingEvents())
        }
    }


    @Test
    fun `stateFlow never completes - turbine behavior`() = runBlockingTest {
        val stateFlow = MutableStateFlow(0)

        stateFlow
            .onCompletion { ex -> println("ON COMPLETE: ${ex?.javaClass?.name}") }
            .test {
                println(awaitItem())
            }
    }

    @Test
    fun `StateFlow - lost initial state`() = runBlocking {
        val hotFlow = MutableStateFlow(42)

        hotFlow.emit(1)

        hotFlow.test {
            assertThat(awaitItem()).isEqualTo(1)
        }
    }

    @Test
    fun `StateFlow - init value collected`() = runBlocking {
        val hotFlow = MutableStateFlow(42)

        hotFlow.test {
            hotFlow.emit(1)

            println(awaitItem())
            println(awaitItem())
            println("Done.")
        }
    }

    // Test thread keeps running forever ...
    @Test
    fun `StateFlow - realistic test`() = runBlockingTest {
        // Arrange (Given)
        val gen = launch {
            genToken() // infinite flow
        }

        // Act (When)
        tokens.test {
            println("token gen launched")
            // Assert (Then)
            println(awaitItem())
            println(awaitItem())
            println(awaitItem())

            gen.cancelAndJoin() // Must use this line.
        }
    }

    @Test
    fun `suspending function version - stateIn`() = runBlockingTest {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(scope = this)

        val subscriber1 = launch {
            given.test {
                assertThat(awaitItem()).isEqualTo(payload)
            }
        }

        val subscriber2 = launch {
            given.test {
                assertThat(awaitItem()).isEqualTo(payload)
            }
        }

        joinAll(subscriber1, subscriber2)
    }

    @Test
    fun `stateIn demo`() = runBlockingTest {
        val payload = 0
        val given = flow {
            emit(payload)
        }.stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = null
        )

        given.test {
            assertThat(awaitItem()).isEqualTo(payload)
        }
    }

}