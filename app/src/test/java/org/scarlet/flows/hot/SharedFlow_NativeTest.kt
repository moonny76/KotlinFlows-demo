package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
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
        val emptyFlow = MutableSharedFlow<Int>()

        val value = withTimeoutOrNull(1000) {
            emptyFlow.first()
        }
        log("value: $value")
        assertThat(value).isNull()
    }

    // hang forever
    @Test
    fun `empty SharedFlow - collect - runBlocking`() = runTest {
        val emptyFlow = MutableSharedFlow<Int>()

        emptyFlow.collect {
            log("try collect ...")
        }

        println("Done.")
    }

    // hang forever
    @Test
    fun `empty SharedFlow - collect in launch - runTest`() = runTest {
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
        val sharedFlow =
            MutableSharedFlow<Int>(replay = 0) // replay = 1, then OK

        sharedFlow.emit(1)

        val value = sharedFlow.first()
        assertThat(value).isEqualTo(1)

        println("Done.")
    }

    @Test
    fun `emit waits for unready collector - SharedFlow`() =
        runBlocking {
            val sharedFlow = MutableSharedFlow<String>(replay = 0)

            val emitter = launch(start = CoroutineStart.LAZY) {
                println("2. Emitter started: Subscription count = ${sharedFlow.subscriptionCount.value}, try to send event ...")
                sharedFlow.emit("Event 1")
                println("3. Emitter: Event 1 sent")

                println("4. Emitter: try to send Event 2")
                sharedFlow.emit("Event 2")
                println("6. Emitter: Event 2 sent")
            }

            val collector = launch {
                println("1. Starts the emitter and subscribes collector which suspends 2000ms")
                emitter.start()

                sharedFlow.collect {
                    delay(3000)
                    if (it == "Event 1")
                        println("5. collected value = $it")
                    else
                        println("7. collected value = $it")
                }
            }

            delay(10_000)

            collector.cancelAndJoin()
            println("Done.")
        }

    @Test
    fun `collectors start to receive data after subscription - no replay`() = runBlocking {
        val sharedFlow = MutableSharedFlow<Int>()

        // Emitter
        launch {
            repeat(5) {
                log("# subscribers = ${sharedFlow.subscriptionCount.value}")
                log("Emit: $it")
                sharedFlow.emit(it)
                log("Emit: $it done")
                delay(200)
            }
        }.onCompletion("Emitter done")

        val collector1 = launch {
            delay(100) // start after 100ms
            log("${spaces(4)}Collector1 subscribes...")
            sharedFlow.collect {
                delay(500)
                log("${spaces(4)}Collector1: $it")
            }
        }.onCompletion("Collector1 done")

        val collector2 = launch {
            delay(300) // start after 300ms
            log("${spaces(8)}Collector2 subscribes...")
            sharedFlow.collect {
                log("${spaces(8)}Collector2: $it")
            }
        }.onCompletion("Collector2 done")

        delay(3000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }
}