package org.scarlet.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.scarlet.util.log

object Buffering {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        // create buffered channel
        val channel = Channel<Int>(4)

        val sender = launch { // launch sender coroutine
            repeat(10) {
                log("Sending $it") // print before sending each element
                channel.send(it) // will suspend when buffer is full
                log("$it sent") // print before sending each element
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
        val channel = Channel<Int>(4).apply { invokeOnClose { log("Channel closed") } }

        launch { // launch sender coroutine
            repeat(10) {
                log("Sending $it") // print before sending each element
                channel.send(it) // will suspend when buffer is full
            }
            log("Closing channel ...")
            channel.close()
        }

        delay(3000)

        for (value in channel) {
            log("$value received")
        }

    }
}