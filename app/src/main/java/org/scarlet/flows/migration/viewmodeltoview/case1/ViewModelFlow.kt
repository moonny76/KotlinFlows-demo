package org.scarlet.flows.migration.viewmodeltoview.case1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource

/**
 * ###1: Expose the result of a one-shot operation with a Mutable data holder
 */
class ViewModelFlow(
    private val query: String,
    private val repository: Repository
) : ViewModel() {

    /* TODO: Change LiveData to StateFlow
    private val _recipes = MutableLiveData<Resource<List<Recipe>>>(Resource.Loading)
    val recipes: LiveData<Resource<List<Recipe>>> = _recipes

    init {
        viewModelScope.launch {
            _recipes.value = repository.getRecipes(query)
        }
    }
    */

    // Version 1: StateFlow
    private val _recipes = MutableStateFlow<Resource<List<Recipe>>>(Resource.Loading)
    val recipes: StateFlow<Resource<List<Recipe>>> = TODO()

    init {
        TODO()
    }

    // Version 2: stateIn
//    val recipes: StateFlow<Resource<List<Recipe>>> = flow {
//        emit(repository.getRecipes(query))
//    }.stateIn(
//        TODO(),
//        TODO(),
//        TODO()
//    )

    // Version 3: SharedFlow
//    private val _recipes = MutableSharedFlow<Resource<List<Recipe>>>(replay = 1)
//    val recipes: SharedFlow<Resource<List<Recipe>>> = TODO()
//
//    init {
//        viewModelScope.launch {
//            TODO()
//        }
//    }

}