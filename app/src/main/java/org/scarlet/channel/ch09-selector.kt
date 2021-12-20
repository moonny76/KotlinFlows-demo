package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select
import kotlin.random.Random

/**
 * Select expression makes it possible to await multiple suspending functions
 * simultaneously and select the first one that becomes available.
 */

@ExperimentalCoroutinesApi
object Selecting_from_channels {

    fun CoroutineScope.fizz() = produce<String> {
        while (true) { // sends "Fizz" every 300 ms
            delay(300)
            send("Fizz")
        }
    }

    fun CoroutineScope.buzz() = produce<String> {
        while (true) { // sends "Buzz!" every 500 ms
            delay(500)
            send("Buzz!")
        }
    }

    suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) {
        select<Unit> { // <Unit> means that this select expression does not produce any result
            fizz.onReceive { value ->  // this is the first select clause
                println("fizz -> '$value'")
            }
            buzz.onReceive { value ->  // this is the second select clause
                println("buzz -> '$value'")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val fizz = fizz()
        val buzz = buzz()

        repeat(7) {
            selectFizzBuzz(fizz, buzz)
        }

        coroutineContext.cancelChildren() // cancel fizz & buzz coroutines
    }
}

/**
 * The `onReceive` clause in select fails when the channel is closed causing the
 * corresponding select to throw an exception. We can use `onReceiveCatching` clause
 * to perform a specific action when the channel is closed.
 */
@ExperimentalCoroutinesApi
object Selecting_on_Close {

    suspend fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String =
        select {
            a.onReceiveCatching { it: ChannelResult<String> ->
                val value = it.getOrNull()
                if (value != null) {
                    "a -> '$value'"
                } else {
                    "Channel 'a' is closed"
                }
            }
            b.onReceiveCatching { it ->
                val value = it.getOrNull()
                if (value != null) {
                    "b -> '$value'"
                } else {
                    "Channel 'b' is closed"
                }
            }
        }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val a = produce {
            repeat(4) { send("Hello $it") }
        }
        val b = produce {
            repeat(4) { send("World $it") }
        }
        repeat(8) { // print first eight results
            println(selectAorB(a, b))
        }
        coroutineContext.cancelChildren()
    }

    /**
     * First of all, select is biased to the first clause. When several clauses
     * are selectable at the same time, the first one among them gets selected.
     *
     * The second observation, is that onReceiveCatching gets immediately
     * selected when the channel is already closed.
     */
}

/**
 * Select expression has `onSend` clause that can be used for a great good
 * in combination with a biased nature of selection.
 */

object Selecting_to_Send_Demo {

    val worker1 = Channel<String>()
    val worker2 = Channel<String>()

    suspend fun dispatch(worker1: SendChannel<String>, worker2: SendChannel<String>) {
        val words = "quick brown fox jumps over the lazy dog".split(" ")

        words.forEach { word ->
            select<Unit> {
                worker1.onSend(word) {
                }
                worker2.onSend(word) {
                }
            }
        }
        worker1.close()
        worker2.close()
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        launch {
            dispatch(worker1, worker2)
        }

        launch {
            worker1.consumeEach {
                delay(100)
                println("worker1: $it")
            }
        }

        launch {
            worker2.consumeEach {
                delay(100)
                println("\t\tworker2: $it")
            }
        }
    }
}

@ExperimentalCoroutinesApi
object Selecting_to_Send {

    fun CoroutineScope.produceNumbers(side: SendChannel<Int>) = produce {
        for (num in 1..10) { // produce 10 numbers from 1 to 10
            delay(100) // every 100 ms
            select<Unit> {
                onSend(num) {} // Send to the primary channel
                side.onSend(num) {} // or to the side channel
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        /**
         * Consumer is going to be quite slow, taking 250 ms to process each number:
         */
        val side = Channel<Int>() // allocate side channel
        launch { // this is a very fast consumer for the side channel
            side.consumeEach { println("Side channel has $it") }
        }

        produceNumbers(side).consumeEach {
            println("Consuming $it")
            delay(250) // let us digest the consumed number properly, do not hurry
        }

        println("Done consuming")
        coroutineContext.cancelChildren()
    }

}
