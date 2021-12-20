package org.scarlet.flows.basics

import org.scarlet.util.delim
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * Find the square of the second even number which is greater than 7.
 */

object List_Eager_Evaluation {

    @JvmStatic
    fun main(args: Array<String>) {
        val list = listOf(8, 7, 10, 3, 6)
            .filter { println("\tfilter: $it"); it > 7 }.also { println("After filter: $it") }
            .filter { println("\tfilter $it"); it % 2 == 0 }.also { println("After filter: $it") }
            .drop(1).also { println("After drop: $it") }
            .map { println("\tmapping $it"); it * it }.also { println("After map: $it") }

        delim("-")

        list.firstOrNull()?.also { println("After firstOrNull: $it") }
    }
}

object Sequence_Lazy_Evaluation {
    @JvmStatic
    fun main(args: Array<String>) {
        val sequence = listOf(8, 7, 10, 3, 6).asSequence()
            .filter { println("\tfilter: $it"); it > 7 }
            .filter { println("\tfilter $it"); it % 2 == 0 }
            .drop(1)
            .map { println("\tmapping $it"); it * it }

        delim("-")

        sequence.firstOrNull()?.also { println("After firstOrNull: $it") }
    }
}

object Flow_Lazy_Evaluation {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val flow = listOf(8, 7, 10, 3, 6).asFlow()
            .filter { println("\tfilter: $it"); it > 7 }
            .filter { println("\tfilter $it"); it % 2 == 0 }
            .drop(1)
            .map { println("\tmapping $it"); it * it }

        delim("-")

        flow.firstOrNull()?.also { println("After firstOrNull: $it") }
    }
}
