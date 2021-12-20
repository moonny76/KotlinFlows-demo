package org.scarlet.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Buffering {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        // create buffered channel
        val channel = Channel<Int>(4)
        val sender = launch { // launch sender coroutine
            repeat(10) {
                println("Sending $it") // print before sending each element
                channel.send(it) // will suspend when buffer is full
            }
        }

        // don't receive anything... just wait....
        delay(1000)
        sender.cancel() // cancel sender coroutine
    }
}

@ExperimentalCoroutinesApi
object Closing_Sender_Waits_until_Received {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        // create buffered channel
        val channel = Channel<Int>(4).apply { invokeOnClose { println("Channel closed") } }
        val sender = launch { // launch sender coroutine
            repeat(10) {
                println("Sending $it") // print before sending each element
                channel.send(it) // will suspend when buffer is full
            }
            println("Closing channel ...")
            channel.close()
        }

        delay(1000)

        for (value in channel) {
            println(value)
        }

    }
}