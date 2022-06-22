package org.scarlet.flows.advanced.a2flattening

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.scarlet.util.log

/**
 * Flattening flows:
 *
 * Flows represent asynchronously received sequences of values, so it is quite
 * easy to get in a situation where each value triggers a request for another
 * sequence of values.
 *
 * For example, we can have the following function that returns a flow of two
 * strings 500 ms apart:
 *
 * fun requestFlow(i: Int): Flow<String> = flow {
 *      emit("$i: First")
 *      delay(500) // wait 500 ms
 *      emit("$i: Second")
 * }
 *
 * Now if we have a flow of three integers and call `requestFlow` for each of them like this:
 *
 *      (1..3).asFlow().map { requestFlow(it) }
 *
 * Then we end up with a flow of flows (`Flow<Flow<String>>`) that needs to be flattened
 * into a single flow for further processing. Collections and sequences have `flatten` and
 * `flatMap` operators for this.
 *
 * However, due to the asynchronous nature of flows they call for different modes of flattening,
 * as such, there is a family of flattening operators on flows.
 */

fun requestFlow(i: Int, timeMs: Long) = flow {
    emit(i to "First")
    delay(timeMs)
    emit(i to "Second")
}

/**
 * flatMapConcat:
 *
 * Concatenating mode is implemented by `flatMapConcat` and `flattenConcat` operators.
 * They are the most direct analogues of the corresponding sequence operators.
 * They wait for the inner flow to complete before starting to collect the next one.
 */
@FlowPreview
object flatmapConcat_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(100) } // a number every 100 ms
            .flatMapConcat { requestFlow(it, 200) }
            .collect { value -> // collect and print
                log("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }

        log(
            (1..3).asFlow()
            .onEach { delay(100) } // a number every 100 ms.
            .flatMapConcat { requestFlow(it, 200) }
            .toList()
        )
    }
}

/**
 * flatMapMerge:
 *
 * Another flattening mode is to concurrently collect all the incoming flows and merge their values
 * into a single flow so that values are emitted as soon as possible. It is implemented by `flatMapMerge`
 * and `flattenMerge` operators. They both accept an optional concurrency parameter that limits the
 * number of concurrent flows that are collected at the same time (it is equal to DEFAULT_CONCURRENCY
 * by default).
 */

//1     2      3
//F         S
//      F          S
//             F         S
@FlowPreview
object flatMapMerge_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(100) }
            .flatMapMerge { requestFlow(it, 150) }
            .collect { value -> // collect and print
                log("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }

        log(
            (1..3).asFlow()
                .onEach { delay(100) } // a number every 100 ms.
                .flatMapMerge { requestFlow(it, 200) }
                .toList()
        )
    }
}

/**
 * flatMapLatest:
 *
 * Similar to the `collectLatest` operator.
 * There is the corresponding "Latest" flattening mode where a collection of the previous flow is cancelled
 * as soon as new flow is emitted.
 */

// x    1    2    3
//      F         S
//                 F          S
//                             F          S
@FlowPreview
@ExperimentalCoroutinesApi
object flatMapLatest_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(100) } // a number every 100 ms
            .flatMapLatest { requestFlow(it, 200) }
            .collect { value -> // collect and print
                log("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }

        log(
            (1..3).asFlow()
                .onEach { delay(100) } // a number every 100 ms.
                .flatMapLatest { requestFlow(it, 200) }
                .toList()
        )
    }
}

@FlowPreview
object flattenConcatDemo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val flow = (1..3).asFlow().map {
            requestFlow(it, 100)
        }.flattenConcat()

        flow.collect {
            log(it)
        }
    }
}

@FlowPreview
object flattenMergeDemo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val flow = (1..3).asFlow().map {
            requestFlow(it, 100)
        }.flattenMerge(4)

        flow.collect {
            log(it)
        }
    }
}
