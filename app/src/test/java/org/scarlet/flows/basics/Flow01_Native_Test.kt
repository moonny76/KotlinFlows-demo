package org.scarlet.flows.basics

import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.counter
import org.scarlet.flows.basics.DataSource.fibo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import java.math.BigInteger

class Flow01_Native_Test {

    @Test
    fun `test finite Flow`(): Unit = runBlocking {
        // Take the first item
        assertThat(
            counter().first()
        ).isEqualTo(0)

        // Take the second item
        assertThat(
            counter().drop(1).first()
        ).isEqualTo(1)

        // Take the first 5 items
        assertThat(
            counter().take(2).toList()
        ).isEqualTo(listOf(0, 1))

        // Take all items
        assertThat(
            counter().toList()
        ).isEqualTo((0..9).toList())

        // Take the first 2 items matching a predicate
        assertThat(
            counter().takeWhile { it < 7 }.toList()
        ).isEqualTo((0..6).toList())

        // Finite data streams
        // Verify that the flow emits exactly N elements (optional predicate)
        assertThat(
            counter().count()
        ).isEqualTo(10)

        assertThat(
            counter().count { it % 2 != 0 }
        ).isEqualTo(5)
    }

    @Test
    fun `test single element flow`() = runBlocking<Unit> {
        // Takes the first item verifying that the flow is closed after that
        try {
            counter().single()
        } catch (ex: Exception) {
            println("Exception caught: $ex")
        }

        counter().drop(9).single()
    }

    @Test
    fun `test infinite flow`(): Unit = runBlocking {
        // Take all items
        val result = withTimeoutOrNull(100) {
            fibo().toList() // hang forever ...
        }
        println("result = $result")

        // Take the first 2 items matching a predicate
        println(fibo().dropWhile { it < BigInteger.valueOf(1_000) }.take(10).toList())
    }

    @Test(expected = NoSuchElementException::class)
    fun `empty cold flow`() = runBlocking<Unit> {
//        val emptyFlow = flowOf<Int>()
        val emptyFlow = emptyFlow<Int>()

        emptyFlow.first()
    }

    @Test
    fun `empty cold flow - firstOrNull`() = runBlocking {
//        val emptyFlow = flowOf<Int>()
        val emptyFlow = emptyFlow<Int>()

        val value = emptyFlow.firstOrNull()
        assertThat(value).isNull()

        println("Done.")
    }

    @Test
    fun `empty cold flow - collect`() = runBlocking {
        val emptyFlow = emptyFlow<Int>()

        emptyFlow.collect { println("I will be skipped") }

        println("Done.")
    }

}