package org.scarlet.flows.migration.viewmodeltoview.case4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.flows.model.User
import org.scarlet.util.Resource
import org.scarlet.util.TestData
import org.scarlet.util.captureValues
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.*

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
            repository.getFavoriteRecipesFlow(any())
        } returns flowOf(Resource.Success(TestData.mFavorites))
    }

    @Test
    fun `testLiveData - with mock observer`() = runTest {
        // Arrange (Given)
        viewModel = ViewModelLive(repository, authManager)

        val liveData = viewModel.favorites
        liveData.observeForever(mockObserver)

        // Act (When)
        // TODO

        // Act (Then)
        verifyOrder {
            mockObserver.onChanged(Resource.Loading)
            mockObserver.onChanged(Resource.Success(TestData.mFavorites))
        }

        liveData.removeObserver(mockObserver)
    }

    @Test
    fun `testLiveData - with captureValues`() = runTest {
        // Arrange (Given)
        viewModel = ViewModelLive(repository, authManager)

        // Act (When)
        viewModel.favorites.captureValues {

            // TODO

            // Act (Then)
            assertThat(values).containsExactly(
                Resource.Loading,
                Resource.Success(TestData.mFavorites)
            )
        }

    }

}