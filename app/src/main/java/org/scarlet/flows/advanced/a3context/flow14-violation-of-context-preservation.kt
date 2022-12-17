package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.scarlet.util.delim
import org.scarlet.util.log

/**
 * ## Wrong emission `withContext`:
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
        log("Started flow in $coroutineContext")

        okFlow().collect { value ->
            log("$value, ${currentCoroutineContext()}")
        }

        delim()

        wrongFlow().collect { value ->
            log("$value, ${currentCoroutineContext()}")
        }
    }

    // `coroutineScope` is OK to use here
    private fun okFlow(): Flow<Int> = flow {
        coroutineScope {
            log("Flow: ${currentCoroutineContext()}")
            emit(withContext(Dispatchers.Default) { delay(100); 42 })
            emit(async { delay(100); 24 }.await())
        }
    }

    private fun wrongFlow(): Flow<Int> = flow {
        // The WRONG way to change context for CPU-consuming code in flow builder
//         GlobalScope.launch { // is prohibited
        withContext(Dispatchers.Default) {
            log("Flow: ${currentCoroutineContext()}")
            delay(1_000) // pretend we are computing it in CPU-consuming way
            emit(42)
        }
    }
}

// Emitting inside a newly launched coroutine is also prohibited!!
object Strange {
    val flow = flow {
        emit(1)
        coroutineScope {
            emit(2)
//            launch { // not allowed
//                log("inside launch")
//                emit(3)
//            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        flow.collect { log(it) }
    }
}


