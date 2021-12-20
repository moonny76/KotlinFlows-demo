package org.scarlet.flows.migration.viewmodeltoview.case5

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*

@ExperimentalCoroutinesApi
class ViewModelFlowTest {
    // SUT
    val viewModel by lazy { ViewModelFlow() }

    @Test
    fun `combine flows`() = runBlockingTest {
        val recipeDataSource = flow {
            repeat(10) {
                emit("Recipe$it")
                delay(1000)
            }
        }

        val categoryDataSource = flow {
            repeat(10) {
                emit("Category$it")
                delay(2000)
            }
        }

        val mergedFlow = viewModel.fetchData(recipeDataSource, categoryDataSource)

        mergedFlow.collect {
            println(it)
        }

    }

}