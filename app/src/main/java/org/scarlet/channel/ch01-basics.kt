package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import org.scarlet.util.log
import org.scarlet.util.onClose
import org.scarlet.util.onCompletion
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@JvmInline
value class Item(val value: Int)

suspend fun makeItem(): Item {
    delay(1000) // simulate some asynchrony
    return Item(Random.nextInt(100))
}

/**
 * List is an eager data structure.
 */
object Motivations {
    private suspend fun getItems(): List<Item> = buildList {
        log("\tBuilding first")
        add(makeItem())
        log("\tBuilding second")
        add(makeItem())
        log("\tBuilding third")
        add(makeItem())
    }

    private fun consumeItems(items: List<Item>) {
        items.forEach {
            log("Do something with $it")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var items: List<Item>?

        val time = measureTimeMillis {
            items = getItems()
            consumeItems(items!!)
        }

        log("time = $time")
    }
}

object Basics {
    private suspend fun getItems(channel: Channel<Item>) {
        channel.send(makeItem())
        log("First sent")
        channel.send(makeItem())
        log("Second sent")
        channel.send(makeItem())
        log("Third sent")
        channel.close()
    }

    private suspend fun consumeItems(channel: Channel<Item>) {
        for (item in channel) {
            log("Do something with $item")
        }
    }

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Item>()

        // List is eager
        val time = measureTimeMillis {
            coroutineScope {
                launch {
                    getItems(channel)
                }
                launch {
                    consumeItems(channel)
                }
            }
        }

        log("after getItems(), time = $time")
    }
}

@ExperimentalCoroutinesApi
object Different_Ways_to_Receive_and_Close_a_Channel {

    private val data = listOf(1, 2, 3, 4, 5)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>().onClose()

        launch {
            data.forEach {
                channel.send(it)
                delay(50)
            }

            log("Sender closing channel")
            channel.close() // comment this out to see what happen?
            log("Is channel closed for send? ${channel.isClosedForSend}")
            log("Is channel closed for receive? ${channel.isClosedForReceive}") // make buffer = 5 after closing channel
        }.onCompletion("Sender")

        launch {
            receiveOneByOne(channel)
//            receiveByIterable(channel)
//            receiveByConsumeEach(channel)
        }.onCompletion("Receiver")
    }

    private suspend fun receiveOneByOne(channel: ReceiveChannel<Int>) {
        while (!channel.isClosedForReceive) {
//        repeat(data.size) {
            log("Received ${channel.receive()}")
            delay(100)
//            channel.cancel()
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByIterable(channel: ReceiveChannel<Int>) {
        // here we print received values using `for` loop (until the channel is closed)
        for (value in channel) {
            log("Received $value")
            delay(100)
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByConsumeEach(channel: ReceiveChannel<Int>) {
        channel.consumeEach {
            log("Received $it")
            delay(100)
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
    }
}

object Sender_Suspends_If_No_Receivers {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        //  without a buffer by default
        val channel = Channel<Int>()

        // Sender
        launch {
            withTimeout(3000) {
                log("Sending 42 ...")
                channel.send(42)
                log("unreachable code")
            }
        }.onCompletion("Sender")
    }
}

object Receiver_Suspends_If_No_Senders {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>()

        // Receiver
        launch {
            withTimeout(3000) {
                log("Waiting for senders to send data")
                log("${channel.receive()} received")
                log("unreachable code")
            }
        }.onCompletion("Receiver")
    }
}

@ExperimentalCoroutinesApi
object OneOfReceivers_Cancell_RendezvousChannel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>().onClose()

        // Sender
        launch {
            var i = 0
            while (!channel.isClosedForSend) {
                channel.send(i++)
                delay(100)
            }
            log("Is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

        // Receiver 1
        launch {
            while (!channel.isClosedForReceive) {
                log("Receiver 1:received = " + channel.receive())
                delay(50)
            }
        }.onCompletion("Receiver 1")

        // Receiver 2 - will cancel the channel
        launch {
            log("Receiver 2: received = " + channel.receive())
            delay(300)

            log("Receiver 2 calls cancel")
            channel.cancel()
        }.onCompletion("Receiver 2")

    }
}
