package org.scarlet.flows.basics

import org.scarlet.util.delim
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

/**
 * Intermediate flow operators:
 *
 * The basic operators have familiar names like `map` and `filter`.
 * The important difference to sequences is that blocks of code inside these operators
 * can call suspending functions.
 */

suspend fun performRequest(request: Int): String {
    delay(1_000) // imitate long-running asynchronous work
    return "response $request"
}

object Map_Operator {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        (1..3).asFlow() // a flow of requests
            .map(::performRequest)
            .collect { response -> log(response) }
    }
}

/**
 * Transform operator:
 *
 * `transform` can be used to imitate simple transformations like `map` and `filter`,
 *  as well as implement more complex transformations. Using the `transform` operator,
 *  we can emit arbitrary values an arbitrary number of times.
 */

object Transform_Operator {
    data class Person(val name: String, val age: Int)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val source = (1..3).asFlow() // a flow of requests
            .transform { request ->
                this.emit("Making request $request")
                this.emit(1234)
                this.emit(Person("John", 33))
                this.emit(performRequest(request))
                delim()
            }

        source.collect { response ->
            log(response)
        }

    }
}

/**
 * Size-limiting operators:
 *
 * Size-limiting intermediate operators like `take` cancel the execution of the flow when the
 * corresponding limit is reached. Cancellation in coroutines is always performed by throwing
 * an exception, so that all the resource-management functions (like try { ... } finally { ... }
 * blocks) operate normally in case of cancellation:
 */

object Take_Operator {

    // DO NOT CATCH EXCEPTIONS INSIDE FLOW BODY!!
    private fun numbers(): Flow<Int> = flow {
        try {
            emit(1)
            emit(2)
            log("This line will not execute")
            emit(3)
        } catch (ex: Exception) {
            log("Exception $ex caught")
        } finally {
            log("Finally in numbers()")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        numbers()
            .take(2) // take only the first two
            .collect { value -> log(value) }
    }
}

