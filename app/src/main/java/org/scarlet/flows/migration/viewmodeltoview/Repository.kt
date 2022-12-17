package org.scarlet.flows.migration.viewmodeltoview

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getRecipes(query: String): Resource<List<Recipe>>

    suspend fun getFavoriteRecipes(userId: String): Resource<List<Recipe>>
    fun getFavoriteRecipesFlow(userId: String): Flow<Resource<List<Recipe>>>
}
