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
         * Looks like firstOrNull never returns null in hot flow ... just wait for new value ....
         */
        try {
            withTimeoutOrNull(1000) {
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
    fun `replay - SharedFlow`() = runBlockingTest {
        val sharedFlow =
            MutableSharedFlow<Int>(replay = 1) // replay = 1, then OK

        sharedFlow.emit(1)

        val value = sharedFlow.first()
        assertThat(value).isEqualTo(1)

        println("Done.")
    }

    @Test
    fun `emit waits for unready collector - SharedFlow`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<String>(replay = 0)

            val emitter = launch(start = CoroutineStart.LAZY) {

                delay(10) // need to allow time for collector to subscribe: when runBlockingTest

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
                    delay(2000)
                    if (it == "Event 1")
                        println("5. collected value = $it")
                    else
                        println("7. collected value = $it")
                }
            }

            delay(5000)

            collector.cancelAndJoin()
            println("Done.")
        }

    @Test
    fun `collectors start to receive data after subscription - no replay`() = runBlockingTest {
        val sharedFlow = MutableSharedFlow<Int>()

        // Emitter
        launch {
            repeat(3) {
                println("# subscribers = ${sharedFlow.subscriptionCount.value}")
                println("Emit: $it")
                sharedFlow.emit(it)
                println("Emit: $it done")
                delay(200)
            }
        }

        val collector1 = launch {
            delay(100) // start after 100ms
            println("${spaces(4)}Collector1 subscribes...")
            sharedFlow.collect {
                delay(500)
                println("${spaces(4)}Collector1: $it")
            }
        }

        val collector2 = launch {
            delay(300) // start after 300ms
            println("${spaces(8)}Collector2 subscribes...")
            sharedFlow.collect {
                println("${spaces(8)}Collector2: $it")
            }
        }

        delay(2000)
        collector1.cancelAndJoin()
        collector2.cancelAndJoin()
    }
}