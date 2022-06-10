package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import org.scarlet.util.delim
import org.scarlet.util.log
import org.scarlet.util.onCompletion

@DelicateCoroutinesApi
object RaceConditionChannel {

    private val fruitArray = arrayOf("Apple", "Banana", "Pear")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<String>()

        // Producer
        launch {
            repeat(6) {
                // Send data in channel
                channel.send(fruitArray[it % 3])
            }
        }

        // Consumers
        repeat(3) {
            launch(Dispatchers.Default) {
                channel.consumeEach { value ->
                    log("Consumer $it: $value")
                }
            }
        }

        delay(1000)
        channel.close()
    }
}

// Similar to Rx PublishSubject
@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
object BroadcastChannelDemo {

    private val fruitArray = arrayOf("Apple", "Banana", "Kiwi", "Pear", "Strawberry")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Note: this channel looses all items that are send to it until
        // the first subscriber appears, unless specified as CONFLATED
        val channel = BroadcastChannel<String>(3)

        // Producer
        repeat(3) {
            // Send data in channel
            log("Sending ${fruitArray[it]}")
            channel.send(fruitArray[it])
            log("${fruitArray[it]} sent")
        } // Loses all before subscription

        delim()

        // Consumers
        repeat(2) {
            launch {
                channel.openSubscription().let { rcvChannel ->
                    log("Consumer $it subscribes ...")
                    for (value in rcvChannel) {
                        log("Consumer $it: $value")
                        delay(it * 2000L)
                    }
                }
            }
        }

        delay(500)

        with (channel) {
            repeat(4) {
                send(fruitArray[it])
                log("${fruitArray[it]} sent")
            }
            close()
        }

        delay(1000)
    }
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
object BroadcastChannel_Buffering_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = BroadcastChannel<Int>(3).apply {
            invokeOnClose { log("Channel closed with $it") }
        }

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
        // Send data in channel
        repeat(10) {
            channel.send(it)
            log("Sent $it")
            delim()
            delay(50)
        }

        launch {
            log("Late Consumer:")
            val result = channel.openSubscription().tryReceive()
            if (result.isFailure) {
                log("Late Consumer: empty result received")
            } else if (result.isClosed) {
                log("Late Consumer: channel already closed")
            } else {
                log("Late Consumer: ${result.getOrNull()}")
            }
            channel.cancel()
        }.onCompletion("Late Consumer")
    }
}

// Similar to Rx ConflatedPublishSubject
// Sender never blocks!
@ObsoleteCoroutinesApi
object ConflatedBroadcastChannel_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = ConflatedBroadcastChannel<Int>().apply {
            invokeOnClose { log("Channel closed!") }
        }

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
        // Send data in channel
        repeat(10) {
            channel.send(it)
            log("Sent $it")
            delim()
            delay(50)
        }

        launch {
            log("Late Consumer:")
            val result = channel.openSubscription().tryReceive()
            if (result.isFailure) {
                log("Late Consumer: empty result received")
            } else if (result.isClosed) {
                log("Late Consumer: channel already closed")
            } else {
                log("Late Consumer: ${result.getOrNull()}")
            }
            channel.close()
        }.onCompletion("Late Consumer")

    }
}
