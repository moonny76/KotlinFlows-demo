package org.scarlet.flows.migration.viewmodeltoview.case2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    // TODO: Change LiveData to StateFLow
    val recipes: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        emit(repository.getRecipes(query))
    }

}