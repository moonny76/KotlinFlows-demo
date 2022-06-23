package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select
import org.scarlet.util.log

/**
 * `select` expression makes it possible to await multiple suspending functions
 * simultaneously and select the first one that becomes available.
 */

@ExperimentalCoroutinesApi
object Selecting_from_channels {

    fun CoroutineScope.fizz(): ReceiveChannel<String> = produce {
        while (true) { // sends "Fizz" every 300 ms
            delay(300)
            send("Fizz")
        }
    }

    fun CoroutineScope.buzz(): ReceiveChannel<String> = produce {
        while (true) { // sends "Buzz!" every 500 ms
            delay(500)
            send("Buzz!")
        }
    }

    suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) {
        select { // <Unit> means that this select expression does not produce any result
            fizz.onReceive { value ->  // this is the first select clause
                log("fizz -> '$value'")
            }
            buzz.onReceive { value ->  // this is the second select clause
                log("buzz -> '$value'")
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
 * The `onReceive` clause in `select` fails when the channel is closed causing the
 * corresponding `select` to throw an exception. We can use `onReceiveCatching` clause
 * to perform a specific action when the channel is closed.
 */
@ExperimentalCoroutinesApi
object Selecting_on_Close {

    suspend fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String =
        select {
            a.onReceiveCatching {
                val value = it.getOrNull()
                if (value != null) {
                    "a -> '$value'"
                } else {
                    "Channel 'a' is closed"
                }
            }
            b.onReceiveCatching {
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
     * First of all, `select` is biased to the first clause. When several clauses
     * are selectable at the same time, the first one among them gets selected.
     *
     * The second observation, is that `onReceiveCatching` gets immediately
     * selected when the channel is already closed.
     */
}

/**
 * `select` expression has `onSend` clause that can be used for a great good
 * in combination with a biased nature of selection.
 */

object Selecting_to_Send_Demo {

    private suspend fun dispatch(worker1: SendChannel<String>, worker2: SendChannel<String>) {
        val words = "quick brown fox jumps over the lazy dog".split(" ")

        words.forEach { word ->
            select {
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

        val worker1 = Channel<String>()
        val worker2 = Channel<String>()

        launch {
            worker1.consumeEach {
                log("worker1: $it")
                delay(100)
            }
        }

        launch {
            worker2.consumeEach {
                log("\t\tworker2: $it")
                delay(200)
            }
        }

        launch {
            dispatch(worker1, worker2)
        }
    }
}

@ExperimentalCoroutinesApi
object Selecting_to_Send {

    private fun CoroutineScope.produceNumbers(side: SendChannel<Int>): ReceiveChannel<Int> = produce {
        for (num in 1..10) { // produce 10 numbers from 1 to 10
            delay(100)
            select {
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
            side.consumeEach { log("Side channel has $it") }
        }

        produceNumbers(side).consumeEach {
            log("Consuming $it")
            delay(250) // let us digest the consumed number properly, do not hurry
        }

        log("Done consuming")
        coroutineContext.cancelChildren()
    }

}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
object MixedChannels {

    private suspend fun selectFizzBuzz(fizz: SendChannel<String>, buzz: ReceiveChannel<String>) {
        val word = "quick brown fox jumps over the lazy dog"

        select {
            fizz.onSend(word) {
                log("To fizz: -> sent: $word")
            }
            buzz.onReceive { value ->
                log("From buzz -> '$value'")
            }
        }
    }

    private fun CoroutineScope.fizz(): SendChannel<String> = actor {
        while (true) { // receives every 300 ms
            receive()
            delay(300)
        }
    }

    private fun CoroutineScope.buzz(): ReceiveChannel<String> = produce {
        while (true) { // sends "Buzz!" every 500 ms
            send("Buzz!")
            delay(500)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val fizz: SendChannel<String> = fizz()
        val buzz: ReceiveChannel<String> = buzz()

        repeat(7) {
            selectFizzBuzz(fizz, buzz)
        }

        coroutineContext.cancelChildren() // cancel fizz & buzz coroutines
    }
}
