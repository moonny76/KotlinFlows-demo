package org.scarlet.flows.migration.viewmodeltoview.case1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.launch

/**
 * #1: Expose the result of a one-shot operation with a Mutable data holder
 */
class ViewModelFlow(
    private val query: String,
    private val repository: Repository
) : ViewModel() {

//    // TODO: Change LiveData to StateFlow
//    private val _recipes = MutableLiveData<Resource<List<Recipe>>>()
//    val recipes: LiveData<Resource<List<Recipe>>> = _recipes
//
//    init {
//        _recipes.value = Resource.Loading
//        viewModelScope.launch {
//            val result = repository.getRecipes(query)
//            _recipes.value = result
//        }
//    }

//    // 1. StateFlow
//    private val _recipes = MutableStateFlow<Resource<List<Recipe>>>(Resource.Loading)
//    val recipes: StateFlow<Resource<List<Recipe>>> = _recipes
//
//    init {
//        viewModelScope.launch {
//            _recipes.value = repository.getRecipes(query)
//        }
//    }

//    // 2. SharedFlow
//    private val _recipes = MutableSharedFlow<Resource<List<Recipe>>>(1)
//    val recipes: SharedFlow<Resource<List<Recipe>>> = _recipes.apply {
//        tryEmit(Resource.Loading)
//        distinctUntilChanged()
//    }
//
//    init {
//        viewModelScope.launch {
//            _recipes.emit(repository.getRecipes(query))
//        }
//    }

    // 3. stateIn
    val recipes: SharedFlow<Resource<List<Recipe>>> = flow {
        emit(repository.getRecipes(query))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.Loading
    )

}