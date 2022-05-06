import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import org.scarlet.util.log

@ExperimentalCoroutinesApi
object Produce_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
//        demoWithPlain()
        demoWithProduce()
    }

    suspend fun demoWithPlain() = coroutineScope {
        val channel: Channel<Int> = Channel()

        // Producer
        launch {
            repeat(5) {
                channel.send(it)
            }
            channel.close() // do not forget to close the channel
        }

        // Consumer
        launch {
            for (value in channel) {
                log(value)
            }
        }
    }

    /**
     * `produce` is a producer coroutine builder.
     */
    suspend fun demoWithProduce() = coroutineScope {
        val channel: ReceiveChannel<Int> = produce {
            repeat(5) {
                channel.send(it)
            }
        }

        // Receiver
        launch {
            for (value in channel) {
                log(value)
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
        squares.consumeEach { log(it) } // extension fun
        log("Done!")
    }
}

