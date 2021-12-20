package org.scarlet.flows.advanced.context

import org.scarlet.util.coroutineInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Wrong emission `withContext`:
 *
 * However, the long-running CPU-consuming code might need to be executed in the context of
 * `Dispatchers.Default` and UI-updating code might need to be executed in the context of
 * `Dispatchers.Main`.
 *
 * Usually, `withContext` is used to change the context in the code using
 * Kotlin coroutines, but code in the `flow { ... }` builder has to honor the context preservation
 * property and is **not** allowed to emit from a different context.
 */

object ViolationOfContextPreservation {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        coroutineInfo(0)

        wrongFlow().collect { value ->
            println(value)
        }
    }

    // coroutineScope is OK
    private fun okFlow(): Flow<Int> = flow {
        coroutineScope {
            emit(async(CoroutineName("child1")) { coroutineInfo(1); delay(100); 42 }.await())
            emit(async { delay(100); 24 }.await())
        }
    }

    private fun wrongFlow(): Flow<Int> = flow {
        // The WRONG way to change context for CPU-consuming code in flow builder
        // GlobalScope.launch { // is prohibited
        // launch(Dispatchers.IO) { // is prohibited
        withContext(Dispatchers.Default) {
            for (i in 1..3) {
                delay(1000) // pretend we are computing it in CPU-consuming way
                emit(i) // emit next value
            }
        }
    }
}

// Launching new coroutine is prohibited!!
object Strange {
    val flow = flow {
        emit(1)
        coroutineScope {
            emit(2)
            launch { // not allowed
                emit(3)
            }
        }
    }
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        flow.flowOn(Dispatchers.IO).collect {
            println(it)
        }
    }
}


