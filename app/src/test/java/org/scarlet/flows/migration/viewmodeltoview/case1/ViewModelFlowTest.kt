package org.scarlet.flows.migration.viewmodeltoview.case1

import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.scarlet.flows.model.Recipe.Companion.mRecipes
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class ViewModelFlowTest {
    // SUT
    lateinit var viewModel: ViewModelFlow

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK
    lateinit var repository: Repository

    @Before
    fun init() {
        MockKAnnotations.init(this)

        coEvery {
            repository.getRecipes(any())
        } coAnswers {
            delay(1_000) // simulate network delay
            Resource.Success(mRecipes)
        }

        viewModel = ViewModelFlow("eggs", repository)
    }

    @Test
    fun `testFlow without turbine`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @ExperimentalTime
    @Test
    fun `test flow wih turbine`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

}