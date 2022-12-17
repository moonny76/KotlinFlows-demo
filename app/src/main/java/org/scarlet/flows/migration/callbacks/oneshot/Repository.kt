package org.scarlet.flows.migration.callbacks.oneshot

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource

interface RecipeCallback {
    fun onReceive(response: Resource<Recipe>)
}

interface Repository {
    @Deprecated(
        message = "Use the suspend equivalent -> suspend fun getRecipe()",
        replaceWith = ReplaceWith("getRecipe(recipeId)")
    )
    fun getRecipeCallback(recipeId: String, callback: RecipeCallback)
}

suspend fun Repository.getRecipe(recipeId: String): Resource<Recipe> {
    // TODO()
    return Resource.Empty
}
