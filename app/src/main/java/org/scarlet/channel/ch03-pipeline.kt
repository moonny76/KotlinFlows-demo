package org.scarlet.channel

import org.scarlet.util.coroutineInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

@ExperimentalCoroutinesApi
object ChannelPipelines {
    fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
        coroutineInfo(0)
        currentCoroutineContext().job.invokeOnCompletion { ex ->
            println("produceNumber completes with $ex")
        }
        var x = 1
        while (true) send(x++) // infinite stream of integers starting from 1
    }

    fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Double> = produce {
        coroutineInfo(0)
        currentCoroutineContext().job.invokeOnCompletion { ex ->
            println("square completes with $ex")
        }
        for (x in numbers) send((x * x).toDouble())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val numbers = produceNumbers() // produces integers from 1 and on
        val squares = square(numbers) // squares integers

        repeat(5) {
            println(squares.receive()) // print first five
        }

        println("Done!") // we are done
        coroutineContext.cancelChildren() // cancel children coroutines
    }
}

/**
 * Sieve of Eratosthenes
 */
@ExperimentalCoroutinesApi
object PipelineExample_Primes {
    private fun CoroutineScope.numbersFrom(start: Int) = produce {
        var x = start
        while (true) send(x++) // infinite stream of integers from start
    }

    private fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
        for (x in numbers) if (x % prime != 0) send(x)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        var cur = numbersFrom(2)
        repeat(10) {
            cur = filter(cur, cur.receive().also { println(it) })
        }
        coroutineContext.cancelChildren() // cancel all children to let main finish
    }
}
