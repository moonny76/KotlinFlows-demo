package org.scarlet.flows.migration.viewmodeltoview.case4

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.scarlet.flows.model.Recipe
import org.scarlet.flows.model.User

/**
 * #4: Observing a stream of data with parameters
 */
@ExperimentalCoroutinesApi
class ViewModelFlow(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

    /* TODO:
       1. Change user from LiveData to Flow.
       2. Change favorites from LiveData to StateFlow. Hint: use `stateIn`

    private val user: LiveData<User> = authManager.observeUser().asLiveData()

    val favorites = user.switchMap { user ->
        liveData {
            emit(Resource.Loading)
            emitSource(repository.getFavoriteRecipesFlow(user.id).asLiveData())
        }
    }
    */
    private val user: Flow<User> = authManager.observeUser()

    val favorites: StateFlow<Resource<List<Recipe>>> = TODO()

}