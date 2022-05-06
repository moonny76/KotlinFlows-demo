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
            GlobalScope.launch {
                channel.consumeEach { value ->
                    println("Consumer $it: $value")
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
object BroadcastChannelDemo1 {

    private val fruitArray = arrayOf("Apple", "Banana", "Kiwi", "Pear", "Strawberry")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = BroadcastChannel<String>(3)

        // Producer
        repeat(5) {
            // Send data in channel
            channel.send(fruitArray[it % fruitArray.size])
        } // Loses all before sub

        // Consumers
        repeat(2) {
            launch {
                channel.openSubscription().let { rcvChannel ->
                    for (value in rcvChannel) {
                        log("Consumer $it: $value")
                    }
                }
            }
        }

        delay(500)

        channel.apply {
            send(fruitArray[3]) // Pear
            send(fruitArray[4]) // Strawberry
        }

        delay(1000)
        channel.close()
    }
}

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
object BroadcastChannel_Demo2 {

    private val fruitArray = arrayOf("Apple", "Banana", "Kiwi", "Pear", "Strawberry")

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = BroadcastChannel<String>(3)

        // Producer
        repeat(3) {
            // Send data in channel
            channel.send(fruitArray[it])
        }

        // Consumers
        repeat(2) {
            launch {
                coroutineContext.job.onCompletion("Consumers")
//                channel.openSubscription().consumeEach { value ->
                channel.consumeEach { value ->
                    log("Consumer $it: $value")
                }
            }
        }

        delay(500)

        channel.apply {
            send(fruitArray[3])
            send(fruitArray[4])
        }

        delay(1000)
        channel.close()
    }
}

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
object BroadcastChannel_Buffering_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = BroadcastChannel<Int>(3)

        // Consumers
        launch {
            channel.consumeEach { value ->
                log("\t\t\tConsumer 0: $value")
                delay(100)
            }
        }

        launch {
            channel.consumeEach { value ->
                log("\t\t\t\t\t\t\tConsumer 1: $value")
                delay(300)
            }
        }

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
            log("Late Consumer")
            channel.consumeEach { value ->
                log("Late Consumer: $value")
            }
            if (channel.openSubscription().tryReceive().isFailure) {
                channel.cancel()
            }
        }
    }
}

// Similar to Rx ConflatedPublishSubject
// Sender never blocks!
@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
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
            channel.consumeEach { value ->
                log("Late Consumer: $value")
//                channel.cancel()
            }
        }.onCompletion("Late Consumer")

        delay(1000)
        channel.close()
    }
}
