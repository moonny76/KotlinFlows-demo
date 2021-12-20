package org.scarlet.flows.advanced.context

import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.swing.Swing

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
        println("Init Display")
    }

    private fun updateDisplay(value: Int) {
        println("display updated with = $value")
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

    private fun log(indent: Int, msg: String) =
        println("${spaces(indent)}[${Thread.currentThread().name}] $msg")

    private fun simple(indent: Int, tag: String): Flow<Int> = flow {
        log(indent, "Started flow for $tag")
        for (i in 1..3) {
            emit(i)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        demoOne()
//        demoTwo()

        println("Done")
    }

    private suspend fun demoOne() = coroutineScope {
        launch {
            log(0, "child: collect")
            simple(2, "child").collect { value -> log(0, "child: $value") }
        }
    }

    private suspend fun demoTwo() = coroutineScope {
        launch {
            log(0, "child1: collect")
            simple(2, "child1").collect { value -> log(0, "child1: $value") }
        }

        launch(Dispatchers.Default) {
            log(5, "child2: collect")
            simple(10, "child2").collect { value -> log(5, "child2: $value") }
        }
    }

}


