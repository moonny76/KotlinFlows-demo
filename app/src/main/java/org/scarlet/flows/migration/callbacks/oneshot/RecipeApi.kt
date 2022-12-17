package org.scarlet.flows.migration.callbacks.oneshot

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.scarlet.flows.model.Recipe
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {
    @GET("api/get")
    fun getRecipe(@Query("rId") recipe_id: String): Call<Recipe>

    companion object {
        private const val API_KEY = ""
        private const val BASE_URL = "https://unknown.com"

        var recipeApi: RecipeApi

        init {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            recipeApi = retrofit.create(RecipeApi::class.java)
        }
    }
}