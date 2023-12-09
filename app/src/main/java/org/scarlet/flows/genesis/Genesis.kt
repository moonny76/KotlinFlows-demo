package org.scarlet.flows.genesis

import kotlinx.coroutines.runBlocking

object ObserverPattern {

    @JvmStatic
    fun main(args: Array<String>) {
        TODO()
    }
}

object MyFlowTypes {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
//        val flow = flow {
//            (1..3).forEach {
//                emit(it)
//            }
//        }
//
//        flow.map {
//            it to it * it
//        }.collect {
//            log(it)
//        }
//
//        delim()
//
//        flow.flatMapConcat {
//            flow {
//                emit(it)
//                emit(it * it)
//            }
//        }.collect {
//            log(it)
//        }
//
//        delim()
//
//        flow {
//            repeat(3) {
//                emit(it)
//                delay(100)
//            }
//        }.flatMapLatest {
//            flow {
//                emit("first: $it")
//                delay(150)
//                emit("second: $it")
//            }
//        }.collect {
//            log(it)
//        }

    }
}