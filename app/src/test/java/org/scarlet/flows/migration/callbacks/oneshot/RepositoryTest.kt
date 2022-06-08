package org.scarlet.flows.migration.callbacks.oneshot

import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class RepositoryTest {

    // SUT
    lateinit var repository: Repository

    @MockK
    lateinit var mockRecipeApi: RecipeApi

    @MockK
    lateinit var mockCall: Call<Recipe>

    @MockK
    lateinit var mockResponse: Response<Recipe>

    @Before
    fun init() {
        MockKAnnotations.init(this)
        repository = DefaultRepository(mockRecipeApi)
    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - success case`() = runTest {
        // Arrange (Given)
        every { mockRecipeApi.getRecipe(any()) } returns mockCall
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns Recipe.recipe1

        // TODO - callback stubbing

        // Act (When)
        repository.getRecipeCallback(Recipe.recipe1.recipeId, object : RecipeCallback {
            override fun onReceive(response: Resource<Recipe>) {
                // Assert (Then)
                Truth.assertThat(response).isEqualTo(Resource.Success(Recipe.recipe1))
            }
        })
    }

    @Test
    fun `getRecipeCallback - one-shot operation with callback - network failure case`() = runTest {
        // Arrange (Given)
        every { mockRecipeApi.getRecipe(any()) } returns mockCall
        val slot = slot<Callback<Recipe>>()
        every {
            mockCall.enqueue(capture(slot))
        } answers {
            slot.captured.onFailure(mockCall, IOException("Network Error"))
        }

        // Act (When)
        repository.getRecipeCallback(Recipe.recipe1.recipeId, object : RecipeCallback {
            override fun onReceive(response: Resource<Recipe>) {
                // Assert (Then)
                Truth.assertThat(response).isEqualTo(Resource.Error("Network Error"))
            }
        })
    }

    @Test
    fun `getRecipe - one-shot operation - success case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }

    @Test
    fun `getRecipe - one-shot operation - failure case`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)

    }
}