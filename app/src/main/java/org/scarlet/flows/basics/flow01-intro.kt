package org.scarlet.flows.basics

import org.scarlet.util.delim
import org.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

/**
 * Notice the following in the code with the Flow:
 *
 *  - A builder function for Flow type is called `flow`.
 *  - Code inside the flow { ... } builder block can suspend.
 *  - The simple function is no longer marked with suspend modifier.
 *  - Values are emitted from the flow using `emit` function.
 *  - Values are collected from the flow using `collect` function.
 */

object SimpleFlow {
    private fun simple(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(2000) // pretend we are doing something useful here
            log("Emitter: emit = $i")
            emit(i) // emit next value
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // collect the flow
        simple().collect { value ->
            log("\t\t Collector: value = $value")
        }
    }
}

@ExperimentalStdlibApi
object List_Blocking_Build_Demo1 {

    private fun compute(i: String): Result<String> {
        Thread.sleep(1000)
        return Result.success(i.lowercase())
    }

    // dynamically build a list
    private fun foo() = buildList {
        var result: Result<String> = compute("A").also {
            log("${spaces(10)}[List] compute A")
        }
        add(result)
        result = compute("B").also {
            log("${spaces(10)}[List] compute B")
        }
        add(result)
        result = compute("C").also {
            log("${spaces(10)}[List] compute C")
        }
        add(result)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                log("Am I blocked? $k")
                delay(500)
            }
        }

        var foo: List<Result<String>>

        val elapsedTime = measureTimeMillis {
            foo = foo()
        }
        log("time elapsed for list request = $elapsedTime")
        delim()

        /**
         * List processing can be done only after list construction is finished
         */

        foo.forEach {
            log("[Main] process next = $it")
            delay(500)
        }
    }
}

@ExperimentalStdlibApi
object List_NonBlocking_Build_Demo {

    private suspend fun compute(i: String): Result<String> {
        delay(1000)
        return Result.success(i.lowercase())
    }

    // dynamically build a list
    private suspend fun foo() = buildList {
        var result: Result<String> = compute("A").also {
            log("${spaces(10)}[List] compute A")
        }
        add(result)
        result = compute("B").also {
            log("${spaces(10)}[List] compute B")
        }
        add(result)
        result = compute("C").also {
            log("${spaces(10)}[List] compute C")
        }
        add(result)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..5) {
                log("Am I blocked? $k")
                delay(1500)
            }
        }

        var foo: List<Result<String>>

        val elapsedTime = measureTimeMillis {
            foo = foo()
        }
        log("time elapsed for list construction = $elapsedTime")
        delim()

        /**
         * List processing can be done only after list construction is finished
         */

        foo.forEach {
            log("[Main] process next = $it")
            delay(1000)
        }
    }
}

@ExperimentalStdlibApi
object Sequence_Demo {

    private fun compute(i: String): Result<String> {
        Thread.sleep(1000)
        return Result.success(i.lowercase())
    }

    /**
     * Inside sequence, we can call only `yield` suspend function, because `block` is
     * a restricted suspending function.
     */
    private fun foo() = sequence {
        var result: Result<String> = compute("A").also {
            log("${spaces(10)}[Sequence] compute A")
        }
        yield(result)
        result = compute("B").also {
            log("${spaces(10)}[Sequence] compute B")
        }
        yield(result)
        result = compute("C").also {
            log("${spaces(10)}[Sequence] compute C")
        }
        yield(result)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                log("Am I blocked? $k")
                delay(500)
            }
        }

        var foo: Sequence<Result<String>>?

        val elapsedTime = measureTimeMillis {
            foo = foo()
        }
        log("time elapsed for request = $elapsedTime")
        delim()

        val iterator = foo?.iterator()

        log("[Main] request next")
        while (iterator?.hasNext() == true) {
            val next = iterator.next()
            log("[Main] process next = $next")
            delay(500)
            log("[Main] request next")
        }
    }
}

object FlowDemo {
    private suspend fun compute(i: String): Result<String> {
        delay(1000) // pretend we are doing something useful here
        return Result.success(i.lowercase())
    }

    private fun foo(): Flow<Result<String>> = flow { // flow builder
        var result: Result<String> = compute("A").also {
            log("${spaces(10)}[Emitter] compute A")
        }
        emit(result)
        result = compute("B").also {
            log("${spaces(10)}[Emitter] compute B")
        }
        emit(result)
        result = compute("C").also {
            log("${spaces(10)}[Emitter] compute C")
        }
        emit(result)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                log("Am I blocked? $k")
                delay(500)
            }
        }

        var foo: Flow<Result<String>>
        val elapsedTime = measureTimeMillis {
            foo = foo()
        }
        log("time elapsed for flow construction = $elapsedTime")
        delim()

        // collect the flow
        foo.collect { value ->
            log("[Main] process next = $value")
            delay(500)
        }
    }
}

/**
 * Flow builders:
 *
 * - The flow { ... } builder is the most basic one.
 * - `flowOf` builder that defines a flow emitting a fixed set of values.
 * - Various collections and sequences can be converted to flows using .asFlow() extension functions.
 */

object Flow_Builders {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        // Creates a flow that produces values from the specified vararg-arguments.
        flowOf(1, 3, 5, 7, 9).collect { log(it) }

        delim()

        // Convert an integer range to a flow
        (1..3).asFlow().collect { log(it) }

        delim()

        flow {
            emit(1)
            emit(2)
            emitAll((100..110).asFlow())
        }.collect { log(it) }
    }

}
