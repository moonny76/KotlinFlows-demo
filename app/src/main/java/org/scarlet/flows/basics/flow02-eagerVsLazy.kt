package org.scarlet.flows.basics

import org.scarlet.util.delim
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.scarlet.util.log

/**
 * Find the square of the second even number which is greater than 7.
 */

object List_Eager_Evaluation {

    @JvmStatic
    fun main(args: Array<String>) {
        val list = listOf(8, 7, 10, 3, 6)
            .filter { log("\tfilter: $it"); it > 7 }.also { log("After filter: $it") }
            .filter { log("\tfilter $it"); it % 2 == 0 }.also { log("After filter: $it") }
            .drop(1).also { log("After drop: $it") }
            .map { log("\tmapping $it"); it * it }.also { log("After map: $it") }

        delim("-")

        list.firstOrNull()?.also { log("After firstOrNull: $it") }
    }
}

object Sequence_Lazy_Evaluation {

    @JvmStatic
    fun main(args: Array<String>) {
        val sequence = listOf(8, 7, 10, 3, 6).asSequence()
            .filter { log("\tfilter ($it > 7): " + if (it > 7) "pass" else "fail"); it > 7 }
            .filter { log("\tfilter ($it isEven): " + if (it % 2 == 0) "pass" else "fail"); it % 2 == 0 }
            .drop(1)
            .map { log("\tmapping $it"); it * it }

        delim("-")

        sequence.firstOrNull()?.also { log("After firstOrNull: $it") }
    }
}

object Flow_Lazy_Evaluation {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        val flow = listOf(8, 7, 10, 3, 6).asFlow()
            .filter { log("\tfilter ($it > 7): " + if (it > 7) "pass" else "fail"); it > 7 }
            .filter { log("\tfilter ($it isEven): " + if (it % 2 == 0) "pass" else "fail"); it % 2 == 0 }
            .drop(1)
            .map { log("\tmapping $it"); it * it }

        delim("-")

        flow.firstOrNull()?.also { log("After firstOrNull: $it") }
    }
}
