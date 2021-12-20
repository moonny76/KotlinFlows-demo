package org.scarlet.flows.migration.callbacks.oneshot

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RepositoryTest {

    // SUT
    @MockK
    lateinit var repository: Repository

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - success case`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - failure case`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipe - one-shot operation - success case`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipe - one-shot operation - failure case`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }
}