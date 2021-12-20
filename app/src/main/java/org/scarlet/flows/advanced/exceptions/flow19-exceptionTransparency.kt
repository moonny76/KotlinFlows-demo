package org.scarlet.flows.advanced.exceptions

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Exception transparency:
 *
 * How can code of the emitter encapsulate its exception handling behavior?
 *
 * Flows must be transparent to exceptions and it is a **violation of the
 * exception transparency** to emit values in the `flow { ... }` builder from
 * inside of a `try/catch` block.
 * This guarantees that a collector throwing an exception can always catch it
 * using `try/catch`.
 *
 * The emitter can use a `catch` operator that preserves this exception
 * transparency and allows encapsulation of its exception handling. The body
 * of the `catch` operator can analyze an exception and react to it in different
 * ways depending on which exception was caught:
 *
 * - Exceptions can be rethrown using throw.
 * - Exceptions can be turned into emission of values using emit from the body of `catch`.
 * - Exceptions can be ignored, logged, or processed by some other code.
 */

object Exception_Transparency_Demo1 {
    fun simple(): Flow<String> =
        flow {
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
        simple()
            .catch { exception -> emit("Caught ... $exception") } // emit on exception
            .collect { value -> println(value) }
    }
}

/**
 * Transparent catch:
 *
 * The `catch` intermediate operator, honoring exception transparency, catches
 * only upstream exceptions (that is an exception from all the operators above
 * `catch`, but not below it). If the block in `collect { ... }` (placed below
 * `catch`) throws an exception then it escapes:
 */
object ExceptionHandling_Using_Catch_Operator {
    private fun <T> Flow<T>.handleErrors(): Flow<T> =
        catch { e -> showErrorMessage(e) }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlow()
                .handleErrors() // inline this
                .collect { error("Failed") }
        } catch (ex: Exception) {
            println("Exception $ex caught .. outside collect")
        }

        println("Done.")
    }
}

/**
 * Catching declarative:
 *
 * We can combine the declarative nature of the `catch` operator with a desire
 * to handle all the exceptions, by moving the body of the `collect` operator
 * into `onEach` and putting it before the `catch` operator. Collection of this
 * flow must be triggered by a call to `collect()` without parameters:
 */
object ExceptionHandling_Declaratively {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        dataFlowThrow()
            .onEach { value -> updateUI(value) }
            .catch { e -> showErrorMessage(e) }
            .collect()

        println("Done.")
    }
}

/**
 * As a finishing touch we can now merge `launch` and `collect` calls using
 * `launchIn` terminal operator, further reducing nesting in this code and
 * turning it into a simple left-to-right sequence of operators:
 */

object ExceptionHandling_together_with_launchIn {

    @JvmStatic
    fun main(args: Array<String>) {
        val scope = CoroutineScope(Job())

        dataFlowThrow()
            .onEach { value -> updateUI(value) }
            .catch { e -> showErrorMessage(e) }
            .launchIn(scope)

        println("Done.")
    }
}


