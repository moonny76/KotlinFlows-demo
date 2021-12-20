package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce

@ExperimentalCoroutinesApi
object FanOut {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        // Single producer
        val channel: ReceiveChannel<Int> = produceNumbers()

        // Multiple consumers
        repeat(5) {
            launchConsumer(it, channel)
        }

        delay(950)
        channel.cancel()
    }

    private fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        var x = 1 // start from 1
        while (true) {
            send(x++) // produce next
            delay(100) // wait 0.1s
        }
    }

    private fun CoroutineScope.launchConsumer(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            println("Consumer #$id received $msg")
        }
    }
}

object FanIn {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val channel = Channel<String>()

        // Multiple producers
        launch { sendString(channel, "Ping", 200L) }
        launch { sendString(channel, "Pong", 500L) }

        // Single consumer
        repeat(10) { // receive first six
            println(channel.receive())
        }

        coroutineContext.cancelChildren() // cancel all children to let main finish
    }

    private suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
        while (true) {
            delay(time)
            channel.send(s)
        }
    }
}