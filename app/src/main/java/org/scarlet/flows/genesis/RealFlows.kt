@file:OptIn(ExperimentalCoroutinesApi::class)

package org.scarlet.flows.genesis

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.scarlet.util.delim
import org.scarlet.util.log

object RealFlowTypes {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val flow = flow {
            (1..3).forEach {
                emit(it)
            }
        }

        flow.map {
            it to it * it
        }.collect {
            log(it)
        }

        delim()

        flow.flatMapConcat {
            flow {
                emit(it)
                emit(it * it)
            }
        }.collect {
            log(it)
        }

        delim()

        flow {
            repeat(3) {
                emit(it)
                delay(100)
            }
        }.flatMapLatest {
            flow {
                emit("first: $it")
                delay(150)
                emit("second: $it")
            }
        }.collect {
            log(it)
        }
    }
}