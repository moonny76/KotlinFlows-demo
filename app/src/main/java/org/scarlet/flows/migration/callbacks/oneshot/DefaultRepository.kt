package org.scarlet.flows.migration.callbacks.oneshot

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class DefaultRepository(
    private val recipeApi: RecipeApi
) : Repository {

    override fun getRecipeCallback(
        recipeId: String,
        callback: (Resource<Recipe>) -> Unit
    ) {
        val call: Call<Recipe> = recipeApi.getRecipe(recipeId)
        call.enqueue(object : Callback<Recipe> {
            override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                if (response.isSuccessful) {
                    callback(Resource.Success(response.body()))
                } else {
                    callback(Resource.Error(response.message()))
                }
            }

            override fun onFailure(call: Call<Recipe>, t: Throwable) {
                callback(Resource.Error(t.message))
            }
        })
    }
}