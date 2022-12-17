package org.scarlet.flows.advanced.a5completion

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.flows.advanced.a4exceptions.dataFlow
import org.scarlet.flows.advanced.a4exceptions.dataFlowThrow
import org.scarlet.util.log

/**
 * ## Flow completion:
 *
 * When flow collection completes (normally or exceptionally) it may need to execute an action.
 * It can be done in two ways: *imperative* or *declarative*.
 *
 * <br></br>
 *
 * ### Declarative handling:
 *
 * For the declarative approach, flow has `onCompletion` intermediate operator that is invoked
 * when the flow has completely collected.
 */

object Declarative_Flow_Completion1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        dataFlow()
            .onCompletion { log("Done by on Completion") }
            .collect { log(it) }
    }
}

/**
 * The key advantage of `onCompletion` is a nullable Throwable parameter of the lambda
 * that can be used to determine whether the flow collection was completed normally or
 * exceptionally.
 */
object Declarative_Flow_Completion2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        dataFlowThrow() // `onCompletion` and `catch` order is SIGNIFICANT!
            .onCompletion { cause -> if (cause != null) log("Flow completed exceptionally") }
            .catch { exception -> log("Caught exception $exception") }
            .collect { log(it) }
    }

    /*
     * The `onCompletion` operator, unlike `catch`, does not handle the exception.
     * But, the exception still flows downstream. It will be delivered to further
     * operators down the flow and can be handled with a `catch` operator.
     */
}

object Declarative_Flow_Completion3 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlow()
                .onCompletion { cause -> log("Flow completed with $cause") }
                .catch { ex -> log("${ex.javaClass.simpleName} caught") }   // only catch upstream exceptions
                .collect { value ->
                    check(value <= 2) { "Crashed on $value" }
                    log(value)
                }
        } catch (ex: Exception) {
            log("Exception caught in catch block: ${ex.javaClass.simpleName}")
        }
    }
}

/**
 * ### Imperative finally block:
 *
 * In addition to try/catch, a collector can also use a finally block to
 * execute an action upon collect completion.
 */

object Imperative_Flow_Completion {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlow().collect { log(it) }
        } finally {
            log("Done")
        }
    }

}
