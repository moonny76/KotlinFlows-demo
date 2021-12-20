import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

@ExperimentalCoroutinesApi
object Produce_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        demoWithPlain()
//        demoWithProduce()
    }

    suspend fun demoWithPlain() = coroutineScope {
        val channel: Channel<Int> = Channel()

        // Producer
        launch {
            repeat(10) {
                channel.send(it)
            }
            channel.close()
        }

        // Consumer
        launch {
            for (value in channel) {
                println(value)
            }
        }
    }

    /**
     * `produce` is a producer coroutine builder.
     */
    suspend fun demoWithProduce() = coroutineScope {
        // Producer
        val channel: ReceiveChannel<Int> = produce {
            (0..10).forEach {
                channel.send(it)
            }
        }

        // Receiver
        launch {
            for (value in channel) {
                println(value)
            }
        }
    }
}

/**
 * All functions that create coroutines are defined as extensions on CoroutineScope,
 * so that we can rely on structured concurrency to make sure that we don't have
 * lingering global coroutines in our application.
 */
@ExperimentalCoroutinesApi
object ChannelProducers {

    private fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..5) send(x * x)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val squares = produceSquares()
        squares.consumeEach { println(it) } // extension fun
        println("Done!")
    }
}

