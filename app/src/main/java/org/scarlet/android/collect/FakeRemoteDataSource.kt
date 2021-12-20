package org.scarlet.android.collect

import org.scarlet.flows.model.Recipe
import java.lang.Thread.sleep

class FakeRemoteDataSource {

    private val mRecipes = mutableMapOf<String, Recipe>()

    fun searchRecipes(query: String): List<Recipe> {
        sleep(FAKE_NETWORK_DELAY)
        return mRecipes.values.toList()
    }

    fun addRecipes(recipes: List<Recipe>) {
        recipes.forEach { recipe ->
            mRecipes[recipe.recipeId] = recipe.copy()
        }
    }

    companion object {
        const val FAKE_NETWORK_DELAY = 3_000L
    }
}
