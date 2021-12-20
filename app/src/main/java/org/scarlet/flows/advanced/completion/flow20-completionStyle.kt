package org.scarlet.flows.advanced.completion

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Flow completion:
 *
 * When flow collection completes (normally or exceptionally) it may need to execute an action.
 * As you may have already noticed, it can be done in two ways: imperative or declarative.
 */

private fun simple(): Flow<Int> = (1..3).asFlow()

private fun simpleFailure(): Flow<Int> = flow {
    emit(1)
    throw RuntimeException()
}

/**
 * Declarative handling:
 *
 * For the declarative approach, flow has `onCompletion` intermediate operator that is invoked
 * when the flow has completely collected.
 */

object Flow_Completion_Declaratively_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        simple()
            .onCompletion { println("Done") }
            .collect { value -> println(value) }
    }
}

/**
 * The key advantage of `onCompletion` is a nullable Throwable parameter of the lambda
 * that can be used to determine whether the flow collection was completed normally or
 * exceptionally.
 *
 * In the following example the simple flow throws an exception after emitting the number 1:
 */

object Flow_Completion_Declaratively_Demo2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        simpleFailure() // `onCompletion` and `catch` order is SIGNIFICANT!
            .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally") }
            .catch { exception -> println("Caught exception $exception") }
            .collect { value -> println(value) }
    }

    /**
     * The `onCompletion` operator, unlike `catch`, does not handle the exception.
     * But, the exception still flows downstream. It will be delivered to further
     * operators down the flow and can be handled with a `catch` operator.
     */
}

/**
 * Successful completion:
 *
 * Another difference with catch operator is that `onCompletion` sees all exceptions and receives
 * a null exception only on successful completion of the upstream flow (without cancellation or failure).
 */

object Flow_Completion_Declaratively3 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple()
                .onCompletion { cause -> println("Flow completed with $cause") }
                .catch { ex -> println("${ex.javaClass.simpleName} caught") }   // only catch upstream exception
                .collect { value ->
                    check(value <= 1) { "Collected $value" }
                    println(value)
                }
        } catch (ex: Exception) {
            println("Exception caught in catch block: ${ex.javaClass.simpleName}")
        }
    }
}

/**
 * Imperative finally block:
 *
 * In addition to try/catch, a collector can also use a finally block to
 * execute an action upon collect completion.
 */

object Flow_Completion_Imperatively {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            simple().collect { value -> println(value) }
        } finally {
            println("Done")
        }
    }

}
