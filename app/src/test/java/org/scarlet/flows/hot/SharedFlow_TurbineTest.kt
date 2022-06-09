package org.scarlet.flows.hot

import app.cash.turbine.test
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
class SharedFlow_TurbineTest {

    /**
     * Hot Flows:
     * Emissions to hot flows that don't have active consumers are dropped.
     * It's important to call test (and therefore have an active collector)
     * on a flow before emissions to a flow are made.
     */

    @Test(expected = TimeoutCancellationException::class)
    fun `wrongTest - SharedFlow`() = runTest {
        val hotFlow = MutableSharedFlow<Int>(replay = 0) // replay = 1, then OK

        hotFlow.emit(1)

        withTimeout(1000){
            hotFlow.test {
                assertThat(awaitItem()).isEqualTo(1) // expectMostRecentItem() of no use
            }
        }
    }

    @Test
    fun `rightTest - SharedFlow`() = runTest {
        val hotFlow = MutableSharedFlow<Int>(replay = 0)

        hotFlow.test {
            hotFlow.emit(1)

            assertThat(awaitItem()).isEqualTo(1)
            // No need to call cancel here ...
            println("Done.")
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

        launch {
            repeat(3) {
                log("# subscribers = ${sharedFlow.subscriptionCount.value}")
                log("Emitting: $it")
                sharedFlow.emit(it)
                log("Emit $it done")
                delay(200)
            }
        }.onCompletion("Emitter done")

        val slowCollector = launch {
            delay(100) // start after 100ms
            log("${spaces(4)}Collector1 subscribes...")
            sharedFlow.test {
                while (true) {
                    log("${spaces(4)}Collector1: got ${awaitItem()}")
                    delay(500)
                }
            }
        }.onCompletion("SlowCollector done")

        val fastCollector = launch {
            delay(300) // start after 300ms
            println("${spaces(8)}Collector2 subscribes...")
            sharedFlow.test {
                while (true) {
                    log("${spaces(8)}Collector2: got ${awaitItem()}")
                }
            }
        }.onCompletion("FastCollector done")

        delay(3000)
        slowCollector.cancelAndJoin()
        fastCollector.cancelAndJoin()
    }
}