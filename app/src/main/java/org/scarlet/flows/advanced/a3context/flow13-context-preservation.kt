package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.delim
import org.scarlet.util.log

/**
 * ## Flow context:
 *
 * Collection of a flow always happens in the context of the calling coroutine.
 * This property of a flow is called **context preservation**.
 * So, by default, code in the flow { ... } builder runs in the context that is provided
 * by a collector of the corresponding flow.
 */

object ContextPreservation_Demo {

    private fun simple(tag: String): Flow<Int> = flow {
        log("Started flow for $tag in ${currentCoroutineContext()}")
        emit(42)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        launch {
            log("Collector1: $coroutineContext")
            simple("Collector1").collect { value ->
                log("Collector1: $value, ${currentCoroutineContext()}")
            }
        }.join()

        delim()

        launch(Dispatchers.Default) {
            log("Collector2: $coroutineContext")
            simple("Collector2").collect { value ->
                log("Collector2: $value, ${currentCoroutineContext()}")
            }
        }.join()
    }

}

/**
 * ### Flow invariant is violated
 */
object Why_Context_Preservation {

    private fun dataFlow(): Flow<Int> = flow {
        withContext(Dispatchers.Default) {
            while (currentCoroutineContext().isActive) {
                delay(1_000) // fake long delay
                emit(42)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        launch {
            initDisplay() // prepare ui
            dataFlow().collect {
                updateDisplay(it) // update ui
            }
        }
    }

    private fun initDisplay() {
        log("Init Display")
    }

    private fun updateDisplay(value: Int) {
        log("display updated with = $value")
    }
}

