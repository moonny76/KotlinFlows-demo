package org.scarlet.flows.migration.viewmodeltoview.case4

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.scarlet.flows.model.Recipe
import org.scarlet.flows.model.User

/**
 * ###4: Observing a stream of data with parameters
 */
@ExperimentalCoroutinesApi
class ViewModelLive(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

    private val user: LiveData<User> = authManager.observeUser().asLiveData()

    val favorites: LiveData<Resource<List<Recipe>>> = user.switchMap { user ->
        liveData {
            emit(Resource.Loading)
            emitSource(repository.getFavoriteRecipesFlow(user.id).asLiveData())
        }
    }

    /*
     * TODO:
     * Or, preferably, process both flows using flatMapLatest and convert
     * the output to LiveData:
     */
//    private val user: Flow<User> = authManager.observeUser()
//
//    val favorites: LiveData<Resource<List<Recipe>>> = user.flatMapLatest { user ->
//            TODO()
//        }.asLiveData()

}