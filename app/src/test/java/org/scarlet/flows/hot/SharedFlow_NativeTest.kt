package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.scarlet.util.log
import org.scarlet.util.onCompletion
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class SharedFlow_NativeTest {
    /**
     * Hot Flows:
     * Emissions to hot flows that don't have active consumers are dropped.
     * It's important to call test (and therefore have an active collector)
     * on a flow before emissions to a flow are made.
     *
     * Hot flow never completes.
     */

    @Test
    fun `empty SharedFlow - timeout`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>() // replay = 0 by default

        val value = withTimeoutOrNull(1000) {
            emptyFlow.first()
        }
        assertThat(value).isNull()
    }

    @Test
    fun `empty SharedFlow - collect - UncompletedCoroutinesError`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>()

        emptyFlow.collect {
            log("try collect ...")
        }

        log("Unreachable Code")
    }

    @Test
    fun `empty SharedFlow - collect in launch - UncompletedCoroutinesError`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>()

        launch {
            emptyFlow.collect {
                log("try collect ...")
            }
        }

        log("Done.")
    }

    @Test
    fun `replay - SharedFlow`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>(replay = 1) // replay = 1, then OK

        sharedFlow.emit(1)

        val value = sharedFlow.first()
        assertThat(value).isEqualTo(1).also {
            log("Done.")
        }

    }

    /**
     * `emit` is suspended if there exist any subscribed collectors which are not ready to collect yet.
     */
    @Test
    fun `collectors start to receive data after subscription - no replay`() = runBlocking {
        val sharedFlow = MutableSharedFlow<Int>( // default config.
            replay = 0,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.SUSPEND
        )

        // Emitter
        launch {
            repeat(5) {
                log("# subscribers = ${sharedFlow.subscriptionCount.value}")
                log("Emitting: $it")
                sharedFlow.emit(it)
                log("Emit: $it done")
                delay(200)
            }
        }.onCompletion("Emitter done")

        val slowCollector = launch {
            delay(100) // start after 100ms
            log("${spaces(4)}Collector1 subscribes...")
            sharedFlow.collect {
                log("${spaces(4)}Collector1: $it")
                delay(500) // <----------------- Slow!!!!!!!!!!!
            }
        }.onCompletion("Collector1 done")

        val fastCollector = launch {
            delay(300) // start after 300ms
            log("${spaces(8)}Collector2 subscribes...")
            sharedFlow.collect {
                log("${spaces(8)}Collector2: $it")
            }
        }.onCompletion("Collector2 done")

        delay(3000)
        slowCollector.cancelAndJoin()
        fastCollector.cancelAndJoin()
    }
}