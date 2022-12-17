package org.scarlet.flows.basics

import org.scarlet.util.delim
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.log

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
    repeat(10) {
        try {
            emit(it + 1)
        } catch (ex: Exception) {
            log("Caught $ex")
            if (ex is CancellationException) {
                throw ex
            }
        }
    }
}

object ToList_first_last_Operators {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        log("(1..10).asFlow() = ${(1..10).asFlow().toList()}")
        log("first() = ${(1..10).asFlow().first()}")
        log("last() = ${(1..10).asFlow().last()}")

        delim()

        log("myFlow = ${myFlow.toList()}")
        log("first() = ${myFlow.first()}")
        log("last() = ${myFlow.last()}")

        delim()

        log("empty list first() = ${emptyList<Int>().asFlow().firstOrNull()}")
        log("empty list first() = ${emptyList<Int>().asFlow().first()}") // NoSuchElementException
    }
}

object FirstOrNull_LastOrNull_Operators {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        log(emptyFlow<Int>().firstOrNull())
        log(emptyFlow<Int>().lastOrNull())
        log((1..100).asFlow().first { it % 5 == 0 })

        delim()

        log(myFlow.first { it % 5 == 0 })
        log(myFlow.firstOrNull { it % 13 == 0 })
        log(myFlow.first { it % 13 == 0 }) // NoSuchElementException
    }
}

object Single_Operator {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        log("flowOf(42).single() = ${flowOf(42).single()}")
        log("flowOf(42, 43).singleOrNull() = ${flowOf(42, 43).singleOrNull()}")
        try {
            log("flowOf(42, 43).single() = ${flowOf(42, 43).single()}")
        } catch (ex: Exception) {
            log("${ex.javaClass.simpleName} caught") // IllegalArgumentException
        }

        delim()

        log(
            "emptyList<Int>().asFlow().singleOrNull() = ${
                emptyList<Int>().asFlow().singleOrNull()
            }"
        )

        try {
            log("emptyList<Int>().asFlow().single() = ${emptyList<Int>().asFlow().single()}")
        } catch (ex: Exception) {
            log("${ex.javaClass.simpleName} caught") // NoSuchElementException
        }

        delim()

        log("(1..10).asFlow().singleOrNull() = ${(1..10).asFlow().singleOrNull()}")

        try {
            log("(1..10).asFlow().single() = ${(1..10).asFlow().single()}")
        } catch (ex: Exception) {
            log("${ex.javaClass.simpleName} caught") // IllegalArgumentException
        }
    }
}

object Reduce_Fold_Operators {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val sum = (1..100).asFlow()
            .reduce { a, b -> a + b }
        log(sum)

        val total = (1..100).asFlow()
            .fold(100) { acc, a -> acc + a }
        log(total)

        /**
         * Exercises: Create a list of integers from 1 to 10 by using `fold`.
         */

    }
}

