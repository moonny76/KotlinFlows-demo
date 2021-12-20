package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.random.Random

@JvmInline
value class Item(val value: Int)

suspend fun makeItem(): Item {
    delay(100) // simulate some asynchronism
    return Item(Random.nextInt(100))
}

object Motivations {
    suspend fun getItems() = buildList {
        println("Building first")
        add(makeItem())
        println("Building second")
        add(makeItem())
        println("Building third")
        add(makeItem())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val startTime = System.currentTimeMillis()

        val items = getItems()

        repeat(items.size) {
            if (it == 0) {
                println("time = ${System.currentTimeMillis() - startTime}")
            }
            println("Do something with ${items[it]}")
        }
    }
}

object Basics {
    suspend fun getItems(channel: Channel<Item>) {
        println("Sending first")
        channel.send(makeItem())
        println("Sending second")
        channel.send(makeItem())
        println("Sending third")
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
                println("time = ${System.currentTimeMillis() - startTime}")
            }
            println("\t\tDo something with $item")
        }
    }
}

object Sender_Suspends_If_No_Receivers {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>()

        launch {
            repeat(1) {
                println("Sending $it ...")
                channel.send(42)
            }
        }
    }
}

object Receiver_Suspends_If_No_Senders {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val channel = Channel<Int>(10)

        launch {
            repeat(9) {
                println("Wait for sending ${it}-th ...")
                channel.send(it)
                println("Sent $it")
            }
        }

        launch {
            repeat(10) {
                println("Wait for receiving ${it}-th ...")
                println("${channel.receive()} received")
            }
        }
    }
}

@ExperimentalCoroutinesApi
object Receiving_and_Closing_Channel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val channel = Channel<Int>().apply {
            invokeOnClose { println("Channel closed with cause = $it") }
        }

        launch {
            repeat(5) {
                channel.send(it)
                delay(50)
            }
            channel.close() // comment it out to see what happen?
            println("Is channel closed for receive? ${channel.isClosedForReceive}") // make buffer = 5
            println("Is channel closed for send? ${channel.isClosedForSend}")
        }.apply { invokeOnCompletion { println("Sender completes with ex = $it") } }

        receiveOneByOne(channel)
        receiveByIterable(channel)
        receiveByConsumeEach(channel)
    }

    suspend fun receiveOneByOne(channel: ReceiveChannel<Int>) {
        while (!channel.isClosedForReceive) {
            println(channel.receive())
            delay(100)
        }
        println("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    suspend fun receiveByIterable(channel: ReceiveChannel<Int>) {
        // here we print received values using `for` loop (until the channel is closed)
        for (item in channel) {
            println(item)
            delay(100)
        }
        println("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

    suspend fun receiveByConsumeEach(channel: ReceiveChannel<Int>) {
        channel.consumeEach {
            println(it)
            delay(100)
        }
        println("Is channel closed for receive? ${channel.isClosedForReceive}")
    }

}

@ExperimentalCoroutinesApi
object ReceiverCancellingRendezvousChannel {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val channel = Channel<Int>().apply {
            invokeOnClose { ex ->
                println("Channel closed with ex = $ex")
            }
        }

        val receiver1 = launch {
            while (!channel.isClosedForReceive) {
                delay(50)
                println("Receiver 1:received = " + channel.receive())
            }
        }.apply {
            invokeOnCompletion { println("Receiver 1 completed with ex = $it") }
        }

        val receiver2 = launch {
            println("Receiver 2: received = " + channel.receive())
            delay(500)
            println("Receiver 2 calls cancel")
            channel.cancel()
        }.apply {
            invokeOnCompletion { println("Receiver 2 completed with ex = $it") }
        }

        val sender = launch {
            while (!channel.isClosedForSend) {
                channel.send(Random.nextInt())
                delay(100)
            }
            println("Is channel closed for send? ${channel.isClosedForSend}")
        }.apply {
            invokeOnCompletion { println("Sender completed with ex = ${it?.javaClass?.name}") }
        }

    }
}




