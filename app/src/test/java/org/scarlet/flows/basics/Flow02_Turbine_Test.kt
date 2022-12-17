package org.scarlet.flows.basics

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.basics.DataSource.counter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test

class Flow02_Turbine_Test {

    @Test
    fun `test finite flow 1`(): Unit = runBlocking {
        // Take the first item
        counter().test {
            assertThat(
                awaitItem()
            ).isEqualTo(0)
            // cancel() requires to consume remaining items
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test finite flow 2`(): Unit = runBlocking {
        // Take the most recent item if exists at this moment
        counter().test {
            assertThat(
                expectMostRecentItem() // unreliable, I think
            ).isEqualTo(9)
        }
    }

    @Test
    fun `test finite Flow 3 - turbine`(): Unit = runBlocking {
        // Take the second item
        counter().test {
            awaitItem()
            assertThat(awaitItem()).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }

        // Take the first 2 items
        counter().test {
            assertThat(awaitItem()).isEqualTo(0)
            assertThat(awaitItem()).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }

        // Take the first 2 items
        counter().take(2).test {
            val items = cancelAndConsumeRemainingEvents()
            println(items)
        }

        // Take all items
        counter().test {
            val events = cancelAndConsumeRemainingEvents()
            println(events)
        }
    }

    @Test
    fun `test finite flow 4`(): Unit = runBlocking {
        // Take the first 2 items matching a predicate
        counter().takeWhile { it < 7 }.test {
            repeat(7) {
                println(awaitItem())
            }
            cancelAndIgnoreRemainingEvents() // Complete
        }
    }

    @Test
    fun `test finite flow 5`() = runBlocking {
        // Finite data streams
        // Verify that the flow emits exactly N elements (optional predicate)
        counter().test {
            assertThat(
                cancelAndConsumeRemainingEvents().size
            ).isEqualTo(11)
        }

        counter().filter { it % 2 != 0 }.test {
            assertThat(
                cancelAndConsumeRemainingEvents().size
            ).isEqualTo(6)
        }
    }

}