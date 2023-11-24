package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.scarlet.util.delim
import org.scarlet.util.log
import org.scarlet.util.onCompletion

class SharedFlow_NativeTest {
    /**
     * Hot Flows:
     * Emissions to hot flows that don't have active consumers are dropped.
     * It's important to call test (and therefore have an active collector)
     * on a flow before emissions to a flow are made.
     *
     * **Hot flow never completes**.
     */
    @Test
    fun `empty SharedFlow - timeout`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>(replay = 0) // default

        val value = withTimeoutOrNull(1_000) {
            emptyFlow.first()
        }
        assertThat(value).isNull()
    }

    @Suppress("UNREACHABLE_CODE")
    @Test // wait for 10 seconds to complete
    fun `empty SharedFlow - collect - UncompletedCoroutinesError`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>()

        emptyFlow.collect {
            log("try collect ...")
        }

        log("Unreachable Code")
    }

    @Test
    fun `replay - SharedFlow`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>(replay = 0) // replay = 1, then OK

        sharedFlow.emit(1)

        val value = sharedFlow.first()
        assertThat(value).isEqualTo(1)

        log("Done.")
    }

    /**
     * `emit` is suspended if there exist any subscribed collectors which are not ready to collect yet.
     */
    @Test
    fun `subscribers start to receive data after subscription`() = runBlocking {
        val sharedFlow = MutableSharedFlow<Int>( // default config.
            replay = 0,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.SUSPEND
        )

        // Publisher
        val publisher = launch {
            repeat(15) {
                log("Emitting: $it (# subscribers = ${sharedFlow.subscriptionCount.value})")
                sharedFlow.emit(it)
                log("Emit: $it done")
                delay(200)
            }
        }.onCompletion("Publisher done")

        // Slow subscriber
        val slowSubscriber = launch {
            delay(100) // start after 100ms
            log("${spaces(4)}Slow Subscriber subscribes...").also { delim() }
            sharedFlow.collect {
                log("${spaces(4)}Slow Subscriber: $it")
                delay(500) // <----------------- Slow!!!!!!!!!!!
            }
        }.onCompletion("Slow Subscriber done")

        // Fast subscriber
        val fastSubscriber = launch {
            delay(300) // start after 300ms
            log("${spaces(8)}Fast Subscriber subscribes...")
            sharedFlow.collect {
                log("${spaces(8)}Fast Subscriber: $it")
            }
        }.onCompletion("Fast Subscriber done")

        delay(2_000)
        slowSubscriber.cancelAndJoin()
        delay(1_000)
        fastSubscriber.cancelAndJoin()

        delay(1_000)
        publisher.cancelAndJoin()
    }
}
