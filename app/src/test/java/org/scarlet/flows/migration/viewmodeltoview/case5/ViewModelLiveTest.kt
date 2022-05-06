package org.scarlet.flows.migration.viewmodeltoview.case5

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.scarlet.flows.CoroutineTestRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*

@ExperimentalCoroutinesApi
class ViewModelLiveTest {
    // SUT
    val viewModel by lazy { ViewModelLive() }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test merge with MediatorLiveData`() = runTest {
        val recipeDataSource = MutableLiveData<String>()
        val categoryDataSource = MutableLiveData<String>()

        val liveData = viewModel.fetchData(recipeDataSource, categoryDataSource)

        var recipeItem: String? = null
        var categoryItem: String? = null

        val observer = Observer<MergedData> {
                when (it) {
                    is MergedData.RecipeData -> recipeItem = it.recipeItem
                    is MergedData.CategoryData -> categoryItem = it.categoryItem
                }

                if (recipeItem != null && categoryItem != null) {
                    // both data is ready, proceed to process them
                    println(Pair(recipeItem, categoryItem))
                }
            }
        liveData.observeForever(observer)

        val job1 = launch {
            repeat(5) {
                recipeDataSource.postValue("Recipe$it")
                delay(1000)
            }
        }

        val job2 = launch {
            repeat(5) {
                categoryDataSource.postValue("Category$it")
                delay(2000)
            }
        }

        joinAll(job1, job2)

        liveData.removeObserver(observer)
    }

}