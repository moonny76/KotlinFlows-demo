package org.scarlet.flows.migration.viewmodeltoview.case1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.scarlet.flows.CoroutineTestRule
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelLiveTest {
    // SUT
    lateinit var viewModel: ViewModelLive

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK
    lateinit var repository: Repository

    @MockK(relaxUnitFun = true)
    lateinit var mockObserver: Observer<Resource<List<Recipe>>>

    @Before
    fun init() {
        // TODO
    }

    @Test
    fun `testLiveData - with mock observer`() = coroutineRule.runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `testLiveData - with getValueForTest`() = coroutineRule.runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `testLiveData - with captureValues`() = coroutineRule.runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

}