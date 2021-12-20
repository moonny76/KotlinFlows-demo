package org.scarlet.flows.migration.viewmodeltoview.case5

import androidx.lifecycle.*
import org.scarlet.flows.migration.viewmodeltoview.case5.MergedData.CategoryData
import org.scarlet.flows.migration.viewmodeltoview.case5.MergedData.RecipeData

class ViewModelLive : ViewModel() {
    private val liveDataMerger = MediatorLiveData<MergedData>()

    fun fetchData(
        recipeDataSource: LiveData<String>,
        categoryDataSource: LiveData<String>
    ): MediatorLiveData<MergedData> {

        liveDataMerger.addSource(recipeDataSource) {
            liveDataMerger.value = RecipeData(it)
        }
        liveDataMerger.addSource(categoryDataSource) {
            liveDataMerger.value = CategoryData(it)
        }
        return liveDataMerger
    }
}


