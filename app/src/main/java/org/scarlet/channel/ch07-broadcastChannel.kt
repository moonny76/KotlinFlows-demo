@file:OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.scarlet.util.delim
import org.scarlet.util.log
import org.scarlet.util.onClose
import org.scarlet.util.onCompletion

object Channel_FanOut_RaceCondition {

    private val fruitArray = arrayOf("Apple", "Banana", "Kiwi", "Orange", "Pear", "Watermelon")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<String>()

        // Producer
        launch {
            repeat(fruitArray.size) {
                // Send data in channel
                channel.send(fruitArray[it])
            }
        }

        // Consumers
        repeat(3) {
            launch {
                channel.consumeEach { value ->
                    log("Consumer $it: $value")
                }
            }
        }

        delay(1_000)
        channel.close()
    }
}

// Similar to Rx PublishSubject
object BroadcastChannel_Bufferring_Demo {

    private val fruitArray = arrayOf("Apple", "Banana", "Kiwi", "Orange", "Pear", "Watermelon")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        // Note: this channel looses all items that are send to it until
        // the first subscriber appears, unless specified as CONFLATED
        val channel = BroadcastChannel<String>(1) // vary capacity 1, 2, 3

        // Producer
        repeat(fruitArray.size) {
            // Send data in channel
            channel.send(fruitArray[it])
            log("${fruitArray[it]} sent")
        } // Loses all before subscription

        delim()

        // Two Consumers
        repeat(2) {
            launch {
                channel.openSubscription().let { rcvChannel ->
                    log("Consumer $it subscribes ...")
                    for (value in rcvChannel) {
                        log("Consumer $it: $value")
                        delay(it * 2000L)
                    }
                }
            }.onCompletion("Consumer $it")
        }

        delay(100)

        // Producer again
        with (channel) {
            repeat(fruitArray.size) {
                send(fruitArray[it])
                log("${fruitArray[it]} sent")
            }
            close() // Even if channel closed, all sent items are still received
        }

    }
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
object BroadcastChannel_LateConsumer_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = BroadcastChannel<Int>(1).onClose() // Change to 1,2,3 to see the difference

        // Two Consumers
        launch {
            channel.consumeEach { value ->
                log("\t\t\tConsumer 0: $value")
                delay(100)
            }
        }.onCompletion("Consumer 0")

        launch {
            channel.consumeEach { value ->
                log("\t\t\t\t\t\t\tConsumer 1: $value")
                delay(300)
            }
        }.onCompletion("Consumer 1")

        delay(50) // allow time for receivers to ready

        // Producer
        repeat(10) {
            channel.send(it)
            log("Sent $it")
            delim()
            delay(50)
        }

        launch {
            val result = channel.openSubscription().tryReceive()
            if (result.isFailure) {
                log("Late Consumer: empty result received")
            } else if (result.isClosed) {
                log("Late Consumer: channel already closed")
            } else {
                log("Late Consumer: ${result.getOrNull()}")
            }
        }.onCompletion("Late Consumer")

        delay(1_000)
        channel.close()
    }
}

// Similar to Rx ConflatedPublishSubject
// Sender never blocks!
object ConflatedBroadcastChannel_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = ConflatedBroadcastChannel<Int>().onClose()

        // Consumers
        launch {
            channel.consumeEach { value ->
                log("\t\t\tConsumer 0: $value")
                delay(100)
            }
        }.onCompletion("Consumer 0")

        launch {
            channel.consumeEach { value ->
                log("\t\t\t\t\t\t\tConsumer 1: $value")
                delay(300)
            }
        }.onCompletion("Consumer 1")

        delay(50) // allow time for receivers to ready

        // Producer
        repeat(10) {
            channel.send(it)
            log("Sent $it")
            delim()
            delay(50)
        }

        launch {
            val result = channel.openSubscription().tryReceive()
            if (result.isFailure) {
                log("Late Consumer: empty result received")
            } else if (result.isClosed) {
                log("Late Consumer: channel already closed")
            } else {
                log("Late Consumer: ${result.getOrNull()}")
            }
        }.onCompletion("Late Consumer")

        delay(1_000)
        channel.close()
    }
}
