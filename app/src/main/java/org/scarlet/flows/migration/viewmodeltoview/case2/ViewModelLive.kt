package org.scarlet.flows.migration.viewmodeltoview.case2

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.Repository
import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource
import org.scarlet.util.log

/**
 * ###2: Expose the result of a one-shot operation without a mutable backing property
 */
class ViewModelLive(
    private val query: String,
    private val repository: Repository
) : ViewModel() {

    val recipes: LiveData<Resource<List<Recipe>>> = liveData {
        emit(Resource.Loading)
        emit(repository.getRecipes(query))
    }

}