package org.scarlet.flows.migration.repotoviewmodel

import androidx.lifecycle.LiveData
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface Repository {

    fun getFavoritesLive(): LiveData<Resource<List<Recipe>>>

    fun getFavoritesFlow(): Flow<Resource<List<Recipe>>>
}