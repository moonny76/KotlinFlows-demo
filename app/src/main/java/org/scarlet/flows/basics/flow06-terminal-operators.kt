package org.scarlet.flows.basics

import org.scarlet.util.delim
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Terminal flow operators:
 *
 * Terminal operators on flows are suspending functions that start a collection of the flow.
 * The `collect` operator is the most basic one, but there are other terminal operators, which
 * can make it easier:
 *  - Conversion to various collections like `toList` and `toSet`.
 *  - Operators to get the `first` value and to ensure that a flow emits a `single` value.
 *  - Reducing a flow to a value with `reduce` and `fold`.
 */

private val myFlow = flow {
    try {
        repeat(10) {
            emit(it + 1)
        }
    } catch (ex: Exception) {
        println("Caught ex: ${ex.javaClass.simpleName}")
    }
}

object toList_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        println((1..10).asFlow().toList())
    }
}

object First_Or_Last_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        println((1..10).asFlow().first())   // last()

        delim()

        println(myFlow.first())             // last()
    }
}

object FirstOrNull_LastOrNull_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        println(emptyFlow<Int>().firstOrNull()) // lastOrNull
        println((1..100).asFlow().first { it % 5 == 0 }) // flow cannot be cancelled!!
        println(myFlow.first { it % 5 == 0 })
    }
}

object Single_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        println(flowOf(42).single())    // singleOrNull

        delim()

        try {
            println((1..10).asFlow().single())
        } catch (ex: Exception) {
            println("Exception $ex caught")
        }

        delim()

        try {
            println(myFlow.single())
        } catch (ex: Exception) {
            println("Exception $ex caught")
        }
    }
}

object Reduce_Fold_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val sum = (1..100).asFlow()
            .map { it * it }
            .reduce { a, b -> a + b } // sum them (terminal operator)
        println(sum)

        val total = (1..100).asFlow()
            .map { it * it }
            .fold(100) { acc, a -> acc + a }
        println(total)
    }
}

