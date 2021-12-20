package org.scarlet.flows.advanced.launching_and_cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Flow cancellation checks:
 *
 * For convenience, the flow builder performs additional `ensureActive` checks
 * for cancellation on each emitted value. It means that a busy loop emitting
 * from a `flow { ... }` is cancellable:
 */

object Flow_Cancellation {
    private fun foo() = flow {
        for (i in 1..5) {
            println("Emitting $i")
            emit(i)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            foo().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        } catch (ex: Exception) {
            println("Exception $ex caught")
        }
    }
}

/**
 * Flow cancellation checks:
 *
 * However, most other flow operators do not do additional cancellation checks on their own
 * for performance reasons. For example, if you use IntRange.asFlow extension to write the same
 * busy loop and don't suspend anywhere, then there are no checks for cancellation:
 */
object UncooperativeFlow_Cause_Failed_Cancellation {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        (1..5).asFlow().collect { value ->
            if (value == 3) cancel()
            println(value)
        }
    }

    /*
     * All numbers from 1 to 5 are collected and cancellation gets detected only before
     * return from runBlocking.
     */
}

/**
 * Making busy flow cancellable
 *
 * In the case where you have a busy loop with coroutines you must explicitly check for cancellation.
 * You can add .onEach { currentCoroutineContext().ensureActive() }, but there is a ready-to-use
 * cancellable operator provided to do that:
 */

object CooperativeFlow_Makes_Flow_Cancellable_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            (1..5).asFlow()
                .onEach { coroutineContext.ensureActive() }
                .collect { value ->
                    if (value == 3) cancel()
                    println(value)
                }
        } catch (ex: Exception) {
            println("Exception $ex caught")
        }
    }
}

object CooperativeFlow_Makes_Flow_Cancellable_Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            (1..5).asFlow().cancellable().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        } catch(ex: Exception) {
            println("Exception $ex caught")
        }
    }
}




