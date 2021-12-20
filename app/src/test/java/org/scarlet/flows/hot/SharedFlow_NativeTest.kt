package org.scarlet.flows.hot

import com.google.common.truth.Truth.assertThat
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
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
    fun `empty SharedFlow - timeout`() = runBlockingTest {
        val emptyFlow = MutableSharedFlow<Int>()

        /**
         * Looks like firstOrNull never returns Null in hot flow ... just wait for new value ....
         */
        try {
            withTimeout(1000) {
                val value = emptyFlow.first()
                assertThat(value).isNull()
            }
        } catch (ex: Exception) {
            println("Caught ${ex.javaClass.simpleName}")
        }

        println("Done.")
    }

    // hang forever
    @Test
    fun `empty SharedFlow - collect - runBlocking`() = runBlocking {
        val emptyFlow = MutableSharedFlow<Int>()

        emptyFlow.collect {
            println("try collect ...")
        }

        println("Done.")
    }

    // This job has not completed yet
    @Test
    fun `empty SharedFlow - collect - runBlockingTest`() = runBlockingTest {
        val emptyFlow = MutableSharedFlow<Int>()

        emptyFlow.collect {
            println("try collect ...")
        }

        println("Done.")
    }

    // This job has not completed yet
    @Test
    fun `empty SharedFlow - collect in launch - runBlockingTest`() = runBlockingTest {
        val emptyFlow = MutableSharedFlow<Int>()

        launch {
            emptyFlow.collect {
                println("try collect ...")
            }
        }.join()

        println("Done.")
    }

    @Test
    fun `replay - SharedFlow`() = runBlocking {
        val sharedFlow =
            MutableSharedFlow<Int>(replay = 1) // replay = 1, then OK

        sharedFlow.emit(1)

        val value = sharedFlow.first()
        assertThat(value).isEqualTo(1)

        println("Done.")
    }

    @Test
    fun `Scenario1-two-collectors-and-three-events`() = runBlockingTest{
        val sharedFlow = MutableSharedFlow<Int>()

        launch {
            repeat(3) {
                println("# subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emit: $it")
                sharedFlow.emit(it)  // when there are no subscribers
                println("Emit: $it done")
                delay(2000)
            }
        }

        val collector1 = launch {
            delay(1000)
            println("${spaces(4)}Collector1 subscribes...")
            sharedFlow.collect {
                println("${spaces(4)}Collector1: $it")
                delay(5000) // suspended
            }
        }

        val collector2 = launch {
            delay(3000)
            println("${spaces(8)}Collector2 subscribes...")
            sharedFlow.collect {
                println("${spaces(8)}Collector2: $it")
            }
        }

        delay(10000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }

    @Test
    fun `emit waits for unready collector - SharedFlow`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<String>(replay = 0)

            val emitter = launch(start = CoroutineStart.LAZY) {
//                delay(10) // need to allow time for collector to subscribe: when runBlockingTest
                println("Subscription count = ${sharedFlow.subscriptionCount.value}")
                println("Emitter: Event 1")
                sharedFlow.emit("Event 1")
            }

            val collector = launch {
                emitter.start()
                println("Collector starts emitter and subscribes...")
                sharedFlow.collect {
                    delay(2000)
                    println("collected value = $it")
                    assertThat(it).isEqualTo("Event 1")
                }
            }

            delay(3000)

            collector.cancelAndJoin()
            println("Done.")
        }
}