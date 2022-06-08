package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.swing.Swing
import org.scarlet.util.delim
import org.scarlet.util.log

// Flow invariant is violated
object Why_Context_Preservation {

    fun dataFlow(): Flow<Int> = flow { // create emitter
        withContext(Dispatchers.Default) {
            while (currentCoroutineContext().isActive) {
                delay(1000) // fake long delay
                emit(42)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        launch(Dispatchers.Swing) { // launch in the main thread
            initDisplay() // prepare ui
            dataFlow().collect {
                withContext(Dispatchers.Swing) {
                    updateDisplay(it) // update ui
                }
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

/**
 * Flow context:
 *
 * Collection of a flow always happens in the context of the calling coroutine.
 * This property of a flow is called **context preservation**.
 * So, by default, code in the flow { ... } builder runs in the context that is provided
 * by a collector of the corresponding flow.
 */

object ContextPreservation_Demo {

    private fun simple(tag: String): Flow<Int> = flow {
        log("Started flow for $tag in ${currentCoroutineContext()}")
        for (i in 1..3) {
            emit(i)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        demoOne()
        delim()
        demoTwo()

        log("Done")
    }

    private suspend fun demoOne() = coroutineScope {
        launch {
            log("collector: collect")
            simple("collector").collect { value -> log("collector: $value") }
        }
    }

    private suspend fun demoTwo() = coroutineScope {
        launch {
            simple("Collector1").collect { value -> log("Collector1: $value") }
        }

        launch(Dispatchers.Default) {
            simple("Collector2").collect { value -> log("Collector2: $value") }
        }
    }

}


