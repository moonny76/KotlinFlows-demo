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

    /**
     * emit is suspended if there exists subscribed collectors which is not ready to collect yet.
     */

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

    @Test(expected = TimeoutCancellationException::class)
    fun `collect from shared flow - fail`() = runBlockingTest {
        val sharedFlow = MutableSharedFlow<String>(replay = 0)

        sharedFlow.emit("Event 1")

        sharedFlow.test {
            assertThat(awaitItem()).isEqualTo("Event 1")
        }
    }

    @Test
    fun `collect from shared flow - collector subscribed, but not ready yet`() = runBlocking {
        val sharedFlow = MutableSharedFlow<String>(replay = 0)

        launch {
            println("Subscription count = ${sharedFlow.subscriptionCount.value}")
            sharedFlow.emit("Event 1") // block emission until ready if not ready subscriber exits
        }

        delay(100) // Uncomment to make no collectors subscribed yet

        sharedFlow.test {
            delay(500) // simulate subscribed, but not ready to collect
            println(awaitItem())
            println("Done.")
        }
    }

    @Test
    fun `collect from shared flow - collector subscribed, and ready`() = runBlocking {
        val sharedFlow = MutableSharedFlow<String>(replay = 0)

        val job = launch(start = CoroutineStart.LAZY) {
            println("Subscription count = ${sharedFlow.subscriptionCount.value}")
            sharedFlow.emit("Event 1")
        }

        sharedFlow.test {
            job.start()
            println(awaitItem())
            println("Done.")
        }
    }

    @Test
    fun `Scenario1-two-collectors-and-three-events`() = runBlockingTest{
        val sharedFlow = MutableSharedFlow<Int>()

        launch {
            repeat(3) {
                println("Emit value $it")
                sharedFlow.emit(it)  // when there are no subscribers
                delay(2000)
            }
        }

        launch {
            delay(1000)
            println("${spaces(4)}Collector1 subscribes...")
            sharedFlow.test {
                delay(5000) // suspended
                println("${spaces(4)}Collector1: got ${awaitItem()}")
                println("${spaces(4)}Collector1: got ${awaitItem()}")
            }
        }

        launch {
            delay(3000)
            println("${spaces(8)}Collector2 subscribes...")
            sharedFlow.test {
                println("${spaces(8)}Collector2: got ${awaitItem()}")
            }
        }

        delay(10000)
    }
}