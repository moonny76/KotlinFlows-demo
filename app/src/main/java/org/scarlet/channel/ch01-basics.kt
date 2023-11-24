package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import org.scarlet.util.delim
import org.scarlet.util.log
import org.scarlet.util.onClose
import org.scarlet.util.onCompletion
import kotlin.random.Random

@JvmInline
value class Item(val value: Int)

suspend fun getItem(): Item {
    delay(1_000) // simulate some asynchrony
    return Item(Random.nextInt(100))
}

/**
 * List is an eager data structure.
 */
object Motivations {
    private suspend fun getItems(): List<Item> = buildList {
        add(getItem())
        log("First added")
        add(getItem())
        log("Second added")
        add(getItem())
        log("Third added")
    }

    private fun consumeItems(items: List<Item>) {
        items.forEach {
            log("Do something with $it")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // List is an eager data structure.
        val list = getItems()

        delim()

        consumeItems(list)
    }
}

@DelicateCoroutinesApi
object Basics {
    private suspend fun produceItems(channel: Channel<Item>) {
        channel.send(getItem())
        log("Sender: First sent")
        channel.send(getItem())
        log("Sender: Second sent")
        channel.send(getItem())
        log("Sender: Third sent")
        channel.close()
    }

    private suspend fun consumeItems(channel: Channel<Item>) {
        // Other style of channel consumption will be shown later...
        while (!channel.isClosedForReceive) {
            log("Receiver: asking for next item")
            val item = channel.receive()
            log("Receiver: do something with $item")
            delim()
        }
    }

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Item>()

        coroutineScope {
            // Sender
            launch {
                produceItems(channel)
            }

            // Receiver
            launch {
                consumeItems(channel)
            }
        }
    }
}

object Sender_Suspends_If_No_Receivers {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        //  No buffering by default
        val channel = Channel<Int>()

        // Sender
        launch {
            withTimeout(3_000) {
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
            withTimeout(3_000) {
                log("Waiting for sender to send data")
                log("${channel.receive()} received")
                log("unreachable code")
            }
        }.onCompletion("Receiver")
    }
}

@DelicateCoroutinesApi
object Different_Ways_to_Receive_and_Close_a_Channel {
    private val data = listOf(1, 2, 3, 4, 5)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>().onClose() // try with buffer size = 5

        launch {
            data.forEach {
                channel.send(it)
                delay(50)
            }

            log("Sender: closing channel")
            channel.close() // comment this out to see what happen?
            log("Sender: is channel closed for send? ${channel.isClosedForSend}")
            log("Sender: is channel closed for receive? ${channel.isClosedForReceive}")
        }.onCompletion("Sender")

        launch {
            receiveOneByOne(channel)
//            receiveByConsumeEach(channel)
//            receiveByIterable(channel)
        }.onCompletion("Receiver")
    }

    private suspend fun receiveOneByOne(channel: ReceiveChannel<Int>) {
        while (!channel.isClosedForReceive) {
            log("Receiver: received ${channel.receive()}")
            delay(100)
//            channel.cancel() // Cancel the channel and see what happen?
            // `cancel()` closes the channel and removes all buffered sent elements from it
        }
        log("Receiver: *is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByConsumeEach(channel: ReceiveChannel<Int>) {
        channel.consumeEach {
            log("Received $it")
            delay(100)
        }
        log("Receiver: *is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByIterable(channel: ReceiveChannel<Int>) {
        // here we print received values using `for` loop (until the channel is closed)
        for (value in channel) {
            log("Received $value")
            delay(100)
        }
        log("Receiver: *is channel closed for receive? ${channel.isClosedForReceive}")
    }
}

@DelicateCoroutinesApi
object One_Of_Receivers_Cancels_Rendezvous_Channel {
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
            log("Sender: is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

        // Receiver 1
        launch {
            while (!channel.isClosedForReceive) {
                log("Receiver 1: received = " + channel.receive())
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
