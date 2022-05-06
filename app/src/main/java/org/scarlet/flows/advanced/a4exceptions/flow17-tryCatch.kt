package org.scarlet.flows.advanced.a4exceptions

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.scarlet.util.log

/**
 * Flow exceptions:
 *
 * Flow collection can complete with an exception when an emitter or code inside the operators
 * throw an exception. There are several ways to handle these exceptions.
 */

/**
 * Everything is caught:
 *
 * The following examples actually catch any exception happening in the emitter
 * or in any intermediate or terminal operators
*/

object TryCatch_Demo1 {
    fun simple() = flow {
        for (i in 1..3) {
            log("Emitting $i")
            emit(i) // emit next value
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple().collect { value ->
                log(value)
                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            log("Caught $e")
        }
    }
}

object TryCatch_Demo2 {

    fun simple(): Flow<String> = flow {
        for (i in 1..3) {
            log("Emitting $i")
            emit(i) // emit next value
        }
    }.map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple().collect { value -> log(value) }
        } catch (e: Throwable) {
            log("Caught $e")
        }
    }
}
