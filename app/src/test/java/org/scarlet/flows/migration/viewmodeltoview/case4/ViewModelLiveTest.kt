package org.scarlet.flows.migration.viewmodeltoview.case4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.flows.model.User
import org.scarlet.util.Resource
import org.scarlet.util.captureValues
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.scarlet.flows.model.Recipe.Companion.mFavorites

@ExperimentalCoroutinesApi
class ViewModelLiveTest {
    // SUT
    private lateinit var viewModel: ViewModelLive

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var repository: Repository

    @MockK
    private lateinit var authManager: AuthManager

    @MockK(relaxed = true)
    lateinit var mockObserver: Observer<Resource<List<Recipe>>>

    @Before
    fun init() {
        MockKAnnotations.init(this)

        every { authManager.observeUser() } returns flowOf(User("A001", "Peter Parker", 33))

        coEvery {
            delay(1000)
            repository.getFavoriteRecipesFlow(any())
        } returns flowOf(Resource.Success(mFavorites))

        viewModel = ViewModelLive(repository, authManager)
    }

    @Test
    fun `testLiveData - with mock observer`() = runTest {
        // Arrange (Given)
        val liveData = viewModel.favorites
        liveData.observeForever(mockObserver)

        // Act (When)
        advanceUntilIdle()

        // Act (Then)
        verifySequence {
            mockObserver.onChanged(Resource.Loading)
            mockObserver.onChanged(Resource.Success(mFavorites))
        }

        liveData.removeObserver(mockObserver)
    }

    @Test
    fun `testLiveData - with captureValues`() = runTest {
        // Arrange (Given)

        // Act (When)
        viewModel.favorites.captureValues {
            advanceUntilIdle()

            // Act (Then)
            assertThat(values).containsExactly(
                Resource.Loading, Resource.Success(mFavorites)
            )
        }

    }

}