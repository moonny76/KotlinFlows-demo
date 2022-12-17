@file:OptIn(ExperimentalCoroutinesApi::class)

package org.scarlet.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.scarlet.util.log
import org.scarlet.util.onCompletion

object ChannelPipelines_Madeup_Demo {
    private val channel1 = Channel<Int>()
    private val channel2 = Channel<Double>()
    private val channel3 = Channel<Pair<Double, Double>>()

    private suspend fun produceNumbers() {
        var x = 1
        while (true) channel1.send(x++) // infinite stream of integers starting from 1
    }

    private suspend fun square() {
        for (x in channel1) channel2.send((x * x).toDouble())
    }

    private suspend fun pair() {
        for (x in channel2) channel3.send(Pair(x, x))
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        launch {
            produceNumbers()
        }
        launch {
            square()
        }
        launch {
            pair()
        }

        repeat(5) {
            log(channel3.receive()) // print first five
        }

        log("Done!") // we are done
        coroutineContext.cancelChildren() // cancel children coroutines
    }
}

object ChannelPipelines {
    private fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        coroutineContext.job.onCompletion("produceNumber")

        var x = 1
        while (true) send(x++) // infinite stream of integers starting from 1
    }

    private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Double> = produce {
        coroutineContext.job.onCompletion("square")

        for (x in numbers) send((x * x).toDouble())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val numbers: ReceiveChannel<Int> = produceNumbers() // produces integers from 1 and on
        val squares: ReceiveChannel<Double> = square(numbers) // squares integers

        repeat(5) {
            log(squares.receive()) // print first five
        }

        log("Done!") // we are done
        coroutineContext.cancelChildren() // cancel children coroutines
    }
}

/**
 * Sieve of Eratosthenes
 */
object PipelineExample_Primes {
    private fun CoroutineScope.numbersFrom(start: Int) = produce {
        var x = start
        while (true) send(x++) // infinite stream of integers from start
    }

    private fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
        for (x in numbers) if (x % prime != 0) send(x)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var current: ReceiveChannel<Int> = numbersFrom(2)
        val primes = buildList {
            repeat(20) {
                val prime = current.receive()
                add(prime)
                current = filter(current, prime)
            }
        }
        log(primes)
        coroutineContext.cancelChildren() // cancel all children to let main finish
    }
}

