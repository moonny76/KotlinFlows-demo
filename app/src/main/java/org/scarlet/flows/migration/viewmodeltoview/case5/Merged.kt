package org.scarlet.flows.migration.viewmodeltoview.case5

sealed class MergedData {
    data class RecipeData(val recipeItem: String) : MergedData()
    data class CategoryData(val categoryItem: String) : MergedData()
}