package org.scarlet.flows

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.scarlet.util.delim
import org.scarlet.util.log

object ObserverPattern {

    @JvmStatic
    fun main(args: Array<String>) {

    }
}

@FlowPreview
@ExperimentalCoroutinesApi
object RealFlowTypes {
    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val flow = flow {
            emit(1)
            emit(2)
            emit(3)
        }

        flow.collect {
            log(it)
        }

        delim()

        flow.map {
            it * it
        }.collect {
            log(it)
        }

        delim()

        flow.flatMapConcat {
            flow {
                emit(Pair(it, it * it))
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