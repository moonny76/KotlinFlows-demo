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
    delay(1000) // simulate some asynchronism
    return Item(Random.nextInt(100))
}

object Motivations {
    private suspend fun getItems() = buildList {
        log("\tBuilding first")
        add(makeItem())
        log("\tBuilding second")
        add(makeItem())
        log("\tBuilding third")
        add(makeItem())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val startTime = System.currentTimeMillis()

        log("before getItems()")
        val items = getItems() // List is eager
        log("after getItems(), time = ${System.currentTimeMillis() - startTime}")

        repeat(items.size) {
            log("Do something with ${items[it]}")
        }
    }
}

object Basics {
    private suspend fun getItems(channel: Channel<Item>) {
        channel.send(makeItem())
        log("\tFirst sent")
        channel.send(makeItem())
        log("\tSecond sent")
        channel.send(makeItem())
        log("\tThird sent")
    }

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Item>() //  without a buffer by default

        val startTime = System.currentTimeMillis()

        log("before getItems()")
        launch {
            getItems(channel)
        }
        log("after getItems(), time = ${System.currentTimeMillis() - startTime}")

        repeat(3) {
            val item = channel.receive()
            log("Do something with $item")
        }
    }
}

object Sender_Suspends_If_No_Receivers {
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        //  without a buffer by default
        val channel = Channel<Int>().apply {
            invokeOnClose { log("Channel closed") }
        }

        val sender = launch {
            log("Sending 42 ...")
            channel.send(42)
            log("This never prints")
        }

        delay(1000)
        sender.cancelAndJoin()
        channel.close()
    }
}

object Receiver_Suspends_If_No_Senders {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>()

        launch {
            repeat(2) {
                log("Try to send ${it}-th ...")
                channel.send(it)
                log("Sent $it")
            }
        }.onCompletion("Sender")

        launch {
            repeat(3) {
                withTimeout(3000) {
                    log("Wait for receiving ${it}-th ...")
                    log("${channel.receive()} received")
                }
            }
        }.onCompletion("Receiver")
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
            channel.close() // comment this out to see what happen?
            log("Is channel closed for receive? ${channel.isClosedForReceive}") // make buffer = 5
            log("Is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

        receiveOneByOne(channel)
//        receiveByIterable(channel)
//        receiveByConsumeEach(channel)
    }

    private suspend fun receiveOneByOne(channel: ReceiveChannel<Int>) {
        while (!channel.isClosedForReceive) {
            log("${channel.receive()} received")
            delay(100)
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByIterable(channel: ReceiveChannel<Int>) {
        // here we print received values using `for` loop (until the channel is closed)
        for (item in channel) {
            log("$item received")
            delay(100)
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    private suspend fun receiveByConsumeEach(channel: ReceiveChannel<Int>) {
        channel.consumeEach {
            log("$it received")
            delay(100)
        }
        log("*Is channel closed for receive? ${channel.isClosedForReceive}")
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
            var i = 0
            while (!channel.isClosedForSend) {
                channel.send(i++)
                delay(100)
            }
            log("Is channel closed for send? ${channel.isClosedForSend}")
        }.onCompletion("Sender")

    }
}
