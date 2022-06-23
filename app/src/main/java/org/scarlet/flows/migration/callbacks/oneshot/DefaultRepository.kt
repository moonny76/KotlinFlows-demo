package org.scarlet.flows.migration.callbacks.oneshot

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class DefaultRepository(private val recipeApi: RecipeApi) : Repository {

    @Deprecated(
        message = "Use the suspend equivalent -> suspend fun getRecipe()",
        replaceWith = ReplaceWith("getRecipe(recipeId)")
    )
    override fun getRecipeCallback(recipeId: String, callback: RecipeCallback) {
        val call: Call<Recipe> = recipeApi.getRecipe(recipeId)
        call.enqueue(object : Callback<Recipe> {
            override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                if (response.isSuccessful) {
                    callback.onReceive(Resource.Success(response.body()))
                } else {
                    callback.onReceive(Resource.Error(response.message()))
                }
            }

            override fun onFailure(call: Call<Recipe>, t: Throwable) {
                callback.onReceive(Resource.Error(t.message))
            }
        })
    }
}