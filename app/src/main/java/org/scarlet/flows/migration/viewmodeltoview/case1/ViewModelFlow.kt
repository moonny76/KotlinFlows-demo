package org.scarlet.flows.migration.viewmodeltoview.case1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource

/**
 * #1: Expose the result of a one-shot operation with a Mutable data holder
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

    // StateFlow
    private val _recipes = MutableStateFlow<Resource<List<Recipe>>>(Resource.Loading)
    val recipes: StateFlow<Resource<List<Recipe>>> = TODO()

    init {
        TODO()
    }

}