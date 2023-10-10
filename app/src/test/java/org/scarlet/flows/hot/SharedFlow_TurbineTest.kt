package org.scarlet.flows.hot

import app.cash.turbine.test
import app.cash.turbine.withTurbineTimeout
import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.scarlet.util.log
import org.scarlet.util.onCompletion
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.milliseconds

class SharedFlow_TurbineTest {

    /**
     * Hot Flows:
     * Emissions to hot flows that don't have active consumers are dropped.
     * It's important to call test (and therefore have an active collector)
     * on a flow before emissions to a flow are made.
     *
     * **Hot flow never completes**.
     */
    @Test
    fun `wrongTest - SharedFlow`() = runBlocking {
        val hotFlow = MutableSharedFlow<Int>(replay = 0)

        hotFlow.emit(1) // will be dropped

        hotFlow.test(timeout = 1_000.milliseconds) { // default == 3 secs
            assertThat(awaitItem()).isEqualTo(1)
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
     * `emit` is suspended if there exist any subscribed subscribers which are not ready to collect yet.
     */
    @Test
    fun `subscribers start to receive data after subscription - wrong`() = runBlocking {
        val sharedFlow = MutableSharedFlow<Int>( // default config.
            replay = 0,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.SUSPEND
        )

        // Publisher
        val publisher = launch {
            repeat(10) {
                log("Emitting: $it (# subscribers = ${sharedFlow.subscriptionCount.value})")
                sharedFlow.emit(it)
                log("Emit $it done")
                delay(200)
            }
        }.onCompletion("Publisher done")

        val slowSubscriber = launch {
            delay(100) // start after 100ms
            log("${spaces(4)}Subscriber1 subscribes...")
            sharedFlow.test {
                while (isActive) {
                    log("${spaces(4)}Subscriber1: ${awaitItem()}")
                    delay(500)
                }
            }
        }.onCompletion("slowSubscriber done")

        val fastSubscriber = launch {
            delay(300) // start after 300ms
            log("${spaces(8)}Subscriber2 subscribes...")
            sharedFlow.test(timeout = INFINITE) {
                while (isActive) {
                    log("${spaces(8)}Subscriber2: ${awaitItem()}")
                }
            }
        }.onCompletion("fastSubscriber done")

        delay(2_000)
        slowSubscriber.cancelAndJoin()
        fastSubscriber.cancelAndJoin()

        delay(1_000)
        publisher.cancelAndJoin()
    }
}