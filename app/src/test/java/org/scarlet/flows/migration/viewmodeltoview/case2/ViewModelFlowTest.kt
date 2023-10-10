package org.scarlet.flows.migration.viewmodeltoview.case2

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.scarlet.flows.model.Recipe.Companion.mRecipes

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
            delay(1_000)
            Resource.Success(mRecipes)
        }

        viewModel = ViewModelFlow("eggs", repository)
    }

    @Test
    fun `test flow without turbine`() = runTest {
        // Arrange (Given)
        // Act (When)
        val resource = viewModel.recipes.take(2).toList()

        // Assert (Then)
        assertThat(resource).containsExactly(
            Resource.Loading, Resource.Success(mRecipes)
        )
    }

    @Test
    fun `test flow wih turbine`() = runTest {
        // Arrange (Given)
        // Act (When)
        viewModel.recipes.test {
            // Assert (Then)
            assertThat(awaitItem()).isEqualTo(Resource.Loading)
            assertThat(awaitItem()).isEqualTo(Resource.Success(mRecipes))
        }
    }
}