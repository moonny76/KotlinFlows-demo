package org.scarlet.flows.advanced.exceptions

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * Flow exceptions:
 *
 * Flow collection can complete with an exception when an emitter or code inside the operators
 * throw an exception. There are several ways to handle these exceptions.
 */

/**
 * Everything is caught:
 *
 * The following examples actually catche any exception happening in the emitter
 * or in any intermediate or terminal operators
*/

object TryCatch_Demo1 {
    fun simple() = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple().collect { value ->
                println(value)
                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }
}

object TryCatch_Demo2 {

    fun simple(): Flow<String> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }.map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple().collect { value -> println(value) }
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }
}
