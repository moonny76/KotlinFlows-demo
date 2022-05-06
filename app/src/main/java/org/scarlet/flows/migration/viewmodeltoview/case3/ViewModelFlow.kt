package org.scarlet.flows.migration.viewmodeltoview.case3

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * #3: One-shot data load with parameters
 */
@ExperimentalCoroutinesApi
class ViewModelFlow(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

    // TODO:
    // 1. Change userId from LiveData to Flow.
    // 2. Change favorites from LiveData to StateFlow. Hint: use `mapLatest` and `stateIn`

    private val userId: LiveData<String> =
        authManager.observeUser().map { user -> user.id }.asLiveData()

    val favorites: LiveData<Resource<List<Recipe>>> =
        userId.switchMap { id ->
            liveData {
                emit(Resource.Loading)
                emit(repository.getFavoriteRecipes(id))
            }
        }

}