package org.scarlet.flows.migration.viewmodeltoview.case3

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.AuthManager
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

/**
 * #3: One-shot data load with parameters
 */
@ExperimentalCoroutinesApi
class ViewModelLive(
    private val repository: Repository,
    private val authManager: AuthManager
) : ViewModel() {

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