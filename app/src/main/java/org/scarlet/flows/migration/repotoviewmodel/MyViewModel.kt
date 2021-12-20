package org.scarlet.flows.migration.repotoviewmodel

import androidx.lifecycle.*
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class MyViewModel(
    private val repository: Repository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    /**
     * Emit N values
     */
    /**
     * LiveData <-- LiveData
     */
    val favoritesLiveFromLive: LiveData<Resource<List<Recipe>>> = repository.getFavoritesLive()

    /**
     * LiveData --> Flow: Use liveData builder
     */
    val favoritesLiveFromFlow: LiveData<Resource<List<Recipe>>> = liveData {
        repository.getFavoritesFlow().collect {
            emit(it)
        }
    }

    // Use asLiveData()
    val favoritesLiveFromFlowAsLive: LiveData<Resource<List<Recipe>>> =
        repository.getFavoritesFlow().asLiveData()

    /**
     * Emit 1 + Emit N values
     */

    /**
     * LiveData <-- LiveData
     */
    // Use iveData builder
    val favoritesLive1NLive: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        emitSource(repository.getFavoritesLive())
    }

    /**
     *  LiveData <-- Flow
     */
    // liveData, asLiveData
    val favoritesLive1NFlowAsLive: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        emitSource(repository.getFavoritesFlow().asLiveData())
    }

    // liveData, collect
    val favoritesLive1NFlow: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        repository.getFavoritesFlow().collect {
            emit(it)
        }
    }

    // onEach, asLiveData
    val favoritesLive1NFlow_Another: LiveData<Resource<List<Recipe>>> =
        repository.getFavoritesFlow()
            .onStart { emit(Resource.Loading) }
            .asLiveData()

    /**
     * Suspend Transformation
     */
    private suspend fun heavyTransformation(resource: Resource<List<Recipe>>): List<String> =
        withContext(dispatcher) {
            delay(1000) // simulate long-computation
            when (resource) {
                is Resource.Success -> resource.data?.map {
                    it.recipeId
                } ?: emptyList()
                else -> emptyList()
            }
        }

    /**
     * LiveData <-- LiveData
     */
    // switchMap and liveData: cannot use `map` because heavy..() is suspending function :-(
    val favoritesLiveTrans: LiveData<List<String>> =
        repository.getFavoritesLive().switchMap { resource ->
            liveData {
                emit(heavyTransformation(resource))
            }
        }

    /**
     * LiveData <-- Flow
     */
    // map, asLiveData
    val favoritesLiveFromFlowTrans: LiveData<List<String>> = repository.getFavoritesFlow().map {
        heavyTransformation(it)
    }.asLiveData()

    /**
     * Flow <-- Flow
     */
    // map
    val favoritesFlowTrans: Flow<List<String>> = repository.getFavoritesFlow().map {
        heavyTransformation(it)
    }
}


