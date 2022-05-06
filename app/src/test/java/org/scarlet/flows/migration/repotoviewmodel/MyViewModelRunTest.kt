package org.scarlet.flows.migration.repotoviewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.model.Recipe
import org.scarlet.util.*
import kotlin.coroutines.ContinuationInterceptor

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
class MyViewModelRunTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule(UnconfinedTestDispatcher())

    // SUT
    lateinit var viewModel: MyViewModel

    @MockK
    lateinit var mockRepository: Repository

    @Before
    fun init() {
        MockKAnnotations.init(this, relaxed = true)

        every {
            mockRepository.getFavoritesLive()
        } returns liveData {
            emit(Resource.Success(TestData.mFavorites))
        }

        every {
            mockRepository.getFavoritesFlow()
        } returns flow {
            emit(Resource.Success(TestData.mFavorites))
        }
    }

    @Test
    fun `emit N values LiveData from LiveData - manual observer`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)
        val liveData = viewModel.favoritesLiveFromLive
        val observer = object : Observer<Resource<List<Recipe>>> {
            override fun onChanged(resource: Resource<List<Recipe>>) {
                // Assert (Then)
                assertThat(resource).isEqualTo(Resource.Success(TestData.mFavorites))
                liveData.removeObserver(this)
            }
        }

        liveData.observeForever(observer)
    }

    @Test
    fun `emit N values LiveData from LiveData`() = runTest {
        // Arrange (Given)
        println(Thread.currentThread().name + " in test")
        println(coroutineContext[ContinuationInterceptor])
        println("00" + coroutineRule.testDispatcher)

        viewModel = MyViewModel(mockRepository)

        // Act (When)
        val resource = viewModel.favoritesLiveFromLive.getValueForTest()

        // Assert (Then)
        assertThat(resource).isEqualTo(Resource.Success(TestData.mFavorites))
    }

    @Test
    fun `emit N values LiveData from Flow - use liveData builder`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)
        val value = viewModel.favoritesLiveFromFlow.getValueForTest()

        // Assert (Then)
        assertThat(value).isEqualTo(Resource.Success(TestData.mFavorites))
    }

    @Test
    fun `emit N values LiveData from Flow - use asLiveData`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)
        val value = viewModel.favoritesLiveFromFlowAsLive.getValueForTest()

        // Assert (Then)
        assertThat(value).isEqualTo(Resource.Success(TestData.mFavorites))
    }

    /**/

    @Test
    fun `emit 1 + N values LiveData from LiveData - with manual Observer`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)
//            val liveData = viewModel.favoritesLive1NLive
//            val observer = object : Observer<Resource<List<Recipe>>> {
//                override fun onChanged(response: Resource<List<Recipe>>?) {
//                    // Assert (Then)
//                    assertThat(response).isEqualTo(Resource.Loading)
//                    assertThat(response).isEqualTo(Resource.Success(TestData.mFavorites))
//                    liveData.removeObserver(this)
//                }
//            }
//            liveData.observeForever(observer)
    }

    @Test
    fun `emit 1 + N values LiveData from LiveData - with mock Observer`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)
        val observer = mockk<Observer<Resource<List<Recipe>>>>(relaxUnitFun = true)

        // Act (When)
        val liveData = viewModel.favoritesLive1NLive
        liveData.observeForever(observer)

        // Assert (Then)
        liveData.removeObserver(observer)
        verify { observer.onChanged(Resource.Loading) }
        verify { observer.onChanged(Resource.Success(TestData.mFavorites)) }
    }

    @Test
    fun `emit 1 + N values LiveData from LiveData - use captureValues`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)
        viewModel.favoritesLive1NLive.captureValues {
            // Assert (Then)
            assertThat(values).containsExactly(
                Resource.Loading,
                Resource.Success(TestData.mFavorites)
            )
        }
    }

    @Test
    fun `emit 1 + N values LiveData from Flow`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        viewModel.favoritesLive1NFlow_Another.captureValues {
            // Assert (Then)
            assertThat(values).containsExactly(
                Resource.Loading,
                Resource.Success(TestData.mFavorites)
            )
        }
    }

    /**/

    @Test
    fun `transform LiveData to LiveData - delay`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository, coroutineRule.testDispatchersProvider)

        // Act (When)
        viewModel.favoritesLiveTrans.captureValues {
            advanceTimeBy(1000); runCurrent()
            // Assert (Then)
            assertThat(values).containsExactly(
                listOf(
                    TestData.recipe1.recipeId,
                    TestData.recipe2.recipeId
                )
            )
        }
    }

    @Test
    fun `transform Flow from Flow`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository, coroutineRule.testDispatchersProvider)

        // Act (When)
        viewModel.favoritesFlowTrans.test {
            advanceTimeBy(1000); runCurrent()
            // Assert (Then)
            assertThat(awaitItem()).isEqualTo(
                listOf(
                    TestData.recipe1.recipeId,
                    TestData.recipe2.recipeId
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}