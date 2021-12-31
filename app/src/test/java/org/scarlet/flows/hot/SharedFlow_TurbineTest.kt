package org.scarlet.flows.hot

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
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
    fun `wrongTest - SharedFlow`() = runBlockingTest {
        val hotFlow = MutableSharedFlow<Int>(replay = 0) // replay = 1, then OK

        hotFlow.emit(1)

        hotFlow.test {
            assertThat(awaitItem()).isEqualTo(1) // expectMostRecentItem() of no use
        }
    }

    @Test
    fun `rightTest - SharedFlow`() = runBlockingTest {
        val hotFlow = MutableSharedFlow<Int>(replay = 0)

        hotFlow.test {
            hotFlow.emit(1)

            assertThat(awaitItem()).isEqualTo(1)
            // No need to call cancel here ...
            println("Done.")
        }
    }

    /**
     * emit is suspended if there exist any subscribed collectors which are not ready to collect yet.
     */

    @Test
    fun `collect from shared flow - collector subscribed, but not ready yet - hard to test using turbine`() = runBlocking {
        val sharedFlow = MutableSharedFlow<Int>(replay = 0)

        val emitter = launch(start = CoroutineStart.LAZY) {
            repeat(3) {
                println("# subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emitter: try to send $it")
                sharedFlow.emit(it)
                println("Emitter: Event $it sent")
            }
        }

        sharedFlow.test {
            println("\t\tCollector subscribes and starts the emitter")
            emitter.start()

            repeat(3) {
                delay(500) // simulate subscribed, but not ready to collect
                println("\t\tCollector received value = ${awaitItem()}")
            }
        }

        println("Done.")
    }

    @Test
    fun `collectors start to receive data after subscription - no replay`() = runBlockingTest {
        val sharedFlow = MutableSharedFlow<Int>()

        launch {
            repeat(3) {
                println("# subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emit: $it")
                sharedFlow.emit(it)  // when there are no subscribers
                println("Emit $it done")
                delay(200)
            }
        }

        launch {
            delay(100)
            println("${spaces(4)}Collector1 subscribes...")
            sharedFlow.test {
                println("${spaces(4)}Collector1: got ${awaitItem()}")
                delay(500)
                println("${spaces(4)}Collector1: got ${awaitItem()}")
            }
        }

        launch {
            delay(300)
            println("${spaces(8)}Collector2 subscribes...")
            sharedFlow.test {
                println("${spaces(8)}Collector2: got ${awaitItem()}")
            }
        }

        delay(3000)
    }
}