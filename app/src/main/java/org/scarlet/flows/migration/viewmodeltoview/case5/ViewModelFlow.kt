package org.scarlet.flows.migration.viewmodeltoview.case5

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow

class ViewModelFlow : ViewModel() {

    // TODO: Merge two Flows into one by combining the latest elements from both flows.
    fun fetchData(
        recipeDataSource: Flow<String>, categoryDataSource: Flow<String>
    ): Flow<Pair<String, String>> = TODO()
}


