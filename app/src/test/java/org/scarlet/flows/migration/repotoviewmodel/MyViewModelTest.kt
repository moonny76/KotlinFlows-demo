package org.scarlet.flows.migration.repotoviewmodel

import com.google.common.truth.Truth.assertThat
import org.scarlet.util.Resource
import org.scarlet.util.TestData
import org.scarlet.util.captureValues
import org.scarlet.util.getValueForTest
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
class MyViewModelTest {

    // SUT
    lateinit var viewModel: MyViewModel

    @MockK
    lateinit var mockRepository: Repository

    @Before
    fun init() {

    }

    @Test
    fun `emit N values LiveData from LiveData - manual observer`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)

        // Assert (Then)
//        assertThat(response).isEqualTo(Resource.Success(TestData.mFavorites))
    }

    @Test
    fun `emit N values LiveData from LiveData`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)

        // Assert (Then)
//        assertThat(resource).isEqualTo(Resource.Success(TestData.mFavorites))
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

        // Act (When)

        // Assert (Then)

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

        // Act (When)

        // Assert (Then)
    }

    /**/

    @Test
    fun `transform LiveData to LiveData - delay`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `transform Flow from Flow`() = runTest {
        // Arrange (Given)
        viewModel = MyViewModel(mockRepository)

        // Act (When)

        // Assert (Then)

    }
}