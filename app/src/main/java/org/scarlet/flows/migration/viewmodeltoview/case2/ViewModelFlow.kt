package org.scarlet.flows.migration.viewmodeltoview.case2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource

/**
 * #2: Expose the result of a one-shot operation without a mutable backing property
 */
class ViewModelFlow(
    private val query: String,
    private val repository: Repository
) : ViewModel() {

    /* TODO: Change LiveData to StateFLow
    val recipes: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        emit(repository.getRecipes(query))
    }
    */

    // 1. StateFlow
    val recipes: StateFlow<Resource<List<Recipe>>> = TODO()

//    // 2. stateIn
//    val recipes: StateFlow<Resource<List<Recipe>>> = flow {
//        emit(Resource.Loading)
//        emit(repository.getRecipes(query))
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.Lazily,
//        initialValue = Resource.Loading
//    )

}