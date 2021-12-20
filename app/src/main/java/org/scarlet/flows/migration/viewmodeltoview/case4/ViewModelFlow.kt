package org.scarlet.flows.migration.viewmodeltoview.case4

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * #4: Observing a stream of data with parameters
 */
@ExperimentalCoroutinesApi
class ViewModelFlow(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

    // TODO:
    // 1. Change userId from LiveData to Flow.
    // 2. Change favorites from LiveData to StateFlow. Hint: use `stateIn`

    private val userId: LiveData<String> =
        authManager.observeUser().map { user -> user.id }.asLiveData()

    val favorites = userId.switchMap { newUserId ->
        liveData {
            emit(Resource.Loading)
            emitSource(repository.getFavoriteRecipesFlow(newUserId).asLiveData())
        }
    }

}