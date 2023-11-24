package org.scarlet.flows.migration.viewmodeltoview.case3

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.flow.*
import org.scarlet.flows.model.User

/**
 * ###3: One-shot data load with parameters
 */
class ViewModelFlow(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

    /* TODO:
     1. Change user from LiveData to Flow.
     2. Change favorites from LiveData to StateFlow.
        Hint: use either `flatMapLatest` or `mapLatest`, and `stateIn`

    private val user: LiveData<User> = authManager.observeUser().asLiveData()

    val favorites: LiveData<Resource<List<Recipe>>> =
        user.switchMap { user ->
            liveData {
                emit(Resource.Loading)
                emit(repository.getFavoriteRecipes(user.id))
            }
        }
     */

    private val user: Flow<User> = authManager.observeUser()

    val favorites: StateFlow<Resource<List<Recipe>>> = TODO()


}