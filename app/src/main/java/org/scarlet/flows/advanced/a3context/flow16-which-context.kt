package org.scarlet.flows.advanced.a3context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.scarlet.util.delim
import org.scarlet.util.log
import kotlin.coroutines.coroutineContext

suspend fun accept(block: suspend () -> Unit) {
    log("coroutineContext2 = $coroutineContext")
    log("currentCoroutineContext2 = ${currentCoroutineContext()}")

    delim()

    val scope = CoroutineScope(Job())
    scope.launch {
        log("coroutineContext3 = $coroutineContext")
        log("currentCoroutineContext3 = ${currentCoroutineContext()}")

        delim()

        block()
    }.join()
}

object WhichContext {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        launch {
            delim()
            log("coroutineContext1 = $coroutineContext")
            log("currentCoroutineContext1 = ${currentCoroutineContext()}")
            delim()

            accept {
                log("coroutineContext4 = $coroutineContext")
                log("currentCoroutineContext4 = ${currentCoroutineContext()}")
                delim()
            }
            delay(1000)
        }.join()

        log("Done.")
    }
}
