package org.scarlet.flows.migration.callbacks.oneshot

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface Repository {
    @Deprecated(
        message = "Use the suspend equivalent -> suspend fun getRecipe()",
        replaceWith = ReplaceWith("getRecipe(recipeId)")
    )
    fun getRecipeCallback(recipeId: String, callback: (Resource<Recipe>) -> Unit)
}

suspend fun Repository.getRecipe(recipeId: String): Resource<Recipe> =
    suspendCoroutine { continuation ->
        getRecipeCallback(recipeId) { continuation.resume(it) }
    }
