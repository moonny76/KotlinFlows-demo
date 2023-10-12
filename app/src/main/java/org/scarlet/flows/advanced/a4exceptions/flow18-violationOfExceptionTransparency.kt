package org.scarlet.flows.advanced.a4exceptions

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

object TryCatch_Review {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlowThrow().collect { updateUI(it) }
        } catch (ex: Throwable) {
            showErrorMessage(ex)
        } finally {
            log("Done.")
        }
    }
}

/**
 * Think about the properties of the flow that is returned by `handleErrors`:
 *
 * ```
 *     val flow = dataFlow().handleErrors()
 * ```
 *
 * It emits some values like any other flow, but it also has an additional property
 * that other flows do not have — any error in the downstream flow is caught by it.
 */
private fun <T> Flow<T>.handleErrors(): Flow<T> = flow {
    try {
        collect { emit(it) }
    } catch (e: Throwable) {
        showErrorMessage(e)
    }
}

/**
 * If we encapsulate this exception-handling logic into an operator on the flow
 * then we can simplify this code, reduce nesting, and make it more readable.
 *
 * This implementation collects values from the upstream flow it is called on
 * and emits them downstream, wrapping the `collect` call into the `try/catch` block.
 *
 * Would it work? Yes, for this particular case. So why exactly this implementation is naive?
 */
object HandleExceptions_Inside_Flow {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        dataFlowThrow()
            .handleErrors()
            .collect { updateUI(it) }

        log("Done.")
    }
}

/**
 * If we run the following code with a simple flow then this code throws
 * an `IllegalStateException` on the first emitted value.
 */
object Exception_Handled_As_Expected {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlow().collect {
                throw IllegalStateException("Failed")
            }
        } catch (e: Throwable) {
            showErrorMessage(e)
        } finally {
            log("Done.")
        }
    }
}

/**
 * But with the flow returned by `handleError` this exception gets caught and
 * does not appear, so `collect` call completes normally. It is totally surprising
 * to the reader of this code who should not be required to know the implementation
 * details of the flow they are trying to collect from.
 *
 * Kotlin flows are designed to allow modular reasoning about data streams.
 * The only supposed effects of flows are their emitted values and completion, so
 * flow operators like `handleError` are not allowed by the flow specification.
 *
 * Every flow implementation has to ensure *exception transparency* — **a downstream
 * exception must always be propagated to the collector**.
 */

object Exception_Swallowed {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            dataFlow()
                .handleErrors()
                .collect {
                    throw IllegalStateException("Failed")
                }
        } catch (ex: Exception) {
            // We want to see `IllegalStateException` exception caught here, but ...
            log("Caught at collector side: $ex")
        }

        log("Done.")
    }
}


