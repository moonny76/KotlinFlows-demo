package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Flows are sequential
 */

fun main() = runBlocking {
    (1..10).asFlow()
            .filter {
                print("Filter $it: ")
                println(if (it % 2 == 0) "pass" else "fail")
                it % 2 == 0
            }
            .map {
                println("\tMap $it")
                "string $it"
            }.collect {
                println("\t\tCollect $it")
            }
}