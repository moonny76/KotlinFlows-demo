package org.scarlet.flows.migration.callbacks.oneshot

import io.mockk.MockKAnnotations
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RepositoryTest {

    // SUT
    lateinit var repository: Repository

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - success case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - network failure case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipe - one-shot operation - success case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipe - one-shot operation - failure case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }
}