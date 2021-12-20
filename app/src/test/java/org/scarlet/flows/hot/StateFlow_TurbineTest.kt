package org.scarlet.flows.hot

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.genToken
import org.scarlet.flows.basics.DataSource.tokens
import org.scarlet.util.coroutineInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.scarlet.util.testDispatcher
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class StateFlow_TurbineTest {

    @Test
    fun `StateFlow - lost first state`() = runBlocking {
        val hotFlow = MutableStateFlow(42)

        hotFlow.emit(1)

        hotFlow.test {
            assertThat(awaitItem()).isEqualTo(1)
            println("Done.")
        }
    }

    @Test
    fun `StateFlow case - init value collected`() = runBlocking {
        val hotFlow = MutableStateFlow(42)

        hotFlow.test {
            hotFlow.emit(1)

            println(awaitItem())
            println(awaitItem())
            println("Done.")
        }
    }

    @Test
    fun testEmission5() = runBlocking {
        val payload = 0
        val given = flow {
            println("flow = ${currentCoroutineContext()}")
            emit(payload)
        }.stateIn(
            scope = this,
//            started = SharingStarted.WhileSubscribed(),
            started = SharingStarted.Eagerly,
            initialValue = null
        )

        given.test {
            println("test = ${currentCoroutineContext()}")
            assertThat(awaitItem()).isNull()
            assertThat(awaitItem()).isEqualTo(payload)
        }
    }

    // See what will happen if `runBlockingTest` used instead - do not use it
    @Test
    fun demo() = runBlockingTest{
        // Arrange (Given)
        val payload = 0
        val given = flow {
            emit(payload)
            delay(1) // if omitte, error
            emit(payload + 1)
            delay(1)
            emit(payload + 2)
        }
//        .flowOn(testDispatcher)
            .stateIn(
            scope = this + Job(),
            initialValue = null,
            started = SharingStarted.WhileSubscribed() // try Eagerly and Lazily
        )

        // Act (When)
        // Assert (Then)
        given.test {
            println(awaitItem())
            println(awaitItem())
            println(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test thread keeps running forever ...
    @Test
    fun `StateFlow - realistic test`() = runBlocking {
        // Arrange (Given)
        val gen = launch {
            coroutineInfo(1)
            // infinite flow
            genToken()
        }

        // Act (When)
        tokens.test {
            coroutineInfo(0)
            println("token gen launched")
            // Assert (Then)
            println(awaitItem())
            println(awaitItem())
            println(awaitItem())
        }

//        gen.cancelAndJoin() // Must use this line.
    }

    @Test
    fun `StateFlow - realistic test2`() = runBlocking {
        // Arrange (Given)
        val scope = CoroutineScope(Job())

        val gen = scope.launch {
            // infinite flow
            try {
                genToken()
            } catch (ex: Exception) {
                println("Caught: $ex")
            }
        }.apply {
            invokeOnCompletion { ex -> println("Completed with $ex") }
        }

        // Act (When)
        tokens.test {
            println("token gen launched")
            // Assert (Then)
            println(awaitItem())
            println(awaitItem())
            println(awaitItem())
            // No need to use cancelAndIgnoreRemainingEvents()
        }

//        gen.cancelAndJoin() // Terminates even without this line
    }

    @Test
    fun `stateFlow never completes - turbine behavior`() = runBlocking {
        val stateFlow = MutableStateFlow(0)

        stateFlow
            .onCompletion { println("ON COMPLETE") }
            .test {
                println(awaitItem())
            }
    }

    @Test
    fun foo1() = runBlocking {
        val flow = MutableStateFlow(1)

        flow.test {
            println(awaitItem())
        }
    }

    @Test
    fun plainTest() = runBlockingTest {
        val mutableStateFlow = MutableStateFlow("empty")
        mutableStateFlow.test {
            mutableStateFlow.value = "1"
            mutableStateFlow.value = "2"
            mutableStateFlow.value = "3"

            assertThat(awaitItem()).isEqualTo("empty")
            assertThat(awaitItem()).isEqualTo("1")
            assertThat(awaitItem()).isEqualTo("2")
            assertThat(awaitItem()).isEqualTo("3")
//            cancelAndConsumeRemainingEvents()
        }
    }

}