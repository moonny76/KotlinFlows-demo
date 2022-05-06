package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import org.scarlet.util.log
import org.scarlet.util.onCompletion
import kotlin.random.Random

@JvmInline
value class Item(val value: Int)

suspend fun makeItem(): Item {
    delay(100) // simulate some asynchronism
    return Item(Random.nextInt(100))
}

object Motivations {
    suspend fun getItems() = buildList {
        log("Building first")
        add(makeItem())
        log("Building second")
        add(makeItem())
        log("Building third")
        add(makeItem())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val startTime = System.currentTimeMillis()

        val items = getItems()

        repeat(items.size) {
            if (it == 0) {
                log("time = ${System.currentTimeMillis() - startTime}")
            }
            log("Do something with ${items[it]}")
        }
    }
}

object Basics {
    suspend fun getItems(channel: Channel<Item>) {
        log("Sending first")
        channel.send(makeItem())
        log("Sending second")
        channel.send(makeItem())
        log("Sending third")
        channel.send(makeItem())
    }

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Item>()

        val startTime = System.currentTimeMillis()
        launch {
            getItems(channel)
        }

        repeat(3) {
            val item = channel.receive()
            if (it == 0) {
                log("time = ${System.currentTimeMillis() - startTime}")
            }
            log("\t\tDo something with $item")
        }
    }
}

object Sender_Suspends_If_No_Receivers {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>()

        val sender = launch {
            log("Sending 42 ...")
            channel.send(42)
            log("This never prints")
        }

        delay(1000)
        sender.cancelAndJoin()
    }
}

object Receiver_Suspends_If_No_Senders {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Int>()

        launch {
            repeat(2) {
                log("Try to send ${it}-th ...")
                channel.send(it)
                log("Sent $it")
            }
        }.onCompletion("Sender")

        val receiver = launch {
            repeat(3) {
                log("Wait for receiving ${it}-th ...")
                log("${channel.receive()} received")
            }
        }.onCompletion("Receiver")

        delay(2000)
        receiver.cancelAndJoin()
    }
}

@ExperimentalCoroutinesApi
object Receiving_and_Closing_Channel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Int>().apply {
            invokeOnClose { log("Channel closed with cause = $it") }
        }

        launch {
            repeat(5) {
                channel.send(it)
                delay(50)
            }
            channel.close() // comment it out to see what happen?
            log("Is channel closed for receive? ${channel.isClosedForReceive}") // make buffer = 5
            log("Is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

//        receiveOneByOne(channel)
//        receiveByIterable(channel)
        receiveByConsumeEach(channel)
    }

    suspend fun receiveOneByOne(channel: ReceiveChannel<Int>) {
        while (!channel.isClosedForReceive) {
            log("${channel.receive()} received")
            delay(100)
        }
        log("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    suspend fun receiveByIterable(channel: ReceiveChannel<Int>) {
        // here we print received values using `for` loop (until the channel is closed)
        for (item in channel) {
            log("${item} received")
            delay(100)
        }
        log("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    suspend fun receiveByConsumeEach(channel: ReceiveChannel<Int>) {
        channel.consumeEach {
            log("${it} received")
            delay(100)
        }
        log("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

}

@ExperimentalCoroutinesApi
object ReceiverCancellingRendezvousChannel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        val channel = Channel<Int>().apply {
            invokeOnClose { ex ->
                log("Channel closed with ex = $ex")
            }
        }

        launch {
            while (!channel.isClosedForReceive) {
                delay(50)
                log("Receiver 1:received = " + channel.receive())
            }
        }.onCompletion("Receiver 1")

        launch {
            log("Receiver 2: received = " + channel.receive())
            delay(500)
            log("Receiver 2 calls cancel")
            channel.cancel()
        }.onCompletion("Receiver 2")

        launch {
            while (!channel.isClosedForSend) {
                channel.send(Random.nextInt())
                delay(100)
            }
            log("Is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

    }
}
