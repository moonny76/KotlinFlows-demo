package org.scarlet.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking

/**
 * This starts an actor that runs on the default dispatcher and records
 * every message sent to it.
 */

@ObsoleteCoroutinesApi
object Actor_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        val printActor: SendChannel<String> = actor(Dispatchers.Default) {
            val messages = mutableListOf<String>()

            for (msg in channel) {
                messages.add(msg)
            }

            println("from thread ${Thread.currentThread()}")
            println(messages.joinToString(", "))
        }

        repeat(10) { i ->
            printActor.send(i.toString())
        }

        printActor.close()
    }
}