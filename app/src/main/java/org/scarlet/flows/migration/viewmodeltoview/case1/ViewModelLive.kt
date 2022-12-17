package org.scarlet.flows.migration.viewmodeltoview.case1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import kotlinx.coroutines.launch

/**
 * ###1: Expose the result of a one-shot operation with a Mutable data holder
 */
class ViewModelLive(
    private val query: String,
    private val repository: Repository
) : ViewModel() {

    private val _recipes = MutableLiveData<Resource<List<Recipe>>>(Resource.Loading)
    val recipes: LiveData<Resource<List<Recipe>>> = _recipes

    init {
        viewModelScope.launch {
            _recipes.value = repository.getRecipes(query)
        }
    }
}