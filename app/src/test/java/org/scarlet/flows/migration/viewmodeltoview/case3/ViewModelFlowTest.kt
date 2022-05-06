package org.scarlet.flows.migration.viewmodeltoview.case3

import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.User
import org.scarlet.util.Resource
import org.scarlet.util.TestData
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class ViewModelFlowTest {
    // SUT
    lateinit var viewModel: ViewModelFlow

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK
    lateinit var repository: Repository

    @MockK
    lateinit var authManager: AuthManager

    @Before
    fun init() {
        MockKAnnotations.init(this)

        every { authManager.observeUser() } returns flowOf(User("A001", "Peter Parker", 33))

        coEvery {
            repository.getFavoriteRecipes(any())
        } coAnswers {
            delay(1000)
            Resource.Success(TestData.mFavorites)
        }
    }

    @Test
    fun testFlow() = runTest {
        // Arrange (Given)
        viewModel = ViewModelFlow(repository, authManager)

        // Act (When)

        // Assert (Then)
//        assertThat(responses).containsExactly(Resource.Loading, Resource.Success(TestData.mFavorites))
    }

    @ExperimentalTime
    @Test
    fun `test flow wih turbine`() = runTest {
        // Arrange (Given)
        viewModel = ViewModelFlow(repository, authManager)

        // Act (When)
//        viewModel.favorites.test {
//            // Assert (Then)
//            assertThat(awaitItem()).isEqualTo(Resource.Loading)
//
//            // Assert (Then)
//            assertThat(awaitItem()).isEqualTo(Resource.Success(TestData.mFavorites))
//        }
    }
}