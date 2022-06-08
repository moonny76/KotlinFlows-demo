package org.scarlet.flows.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey
    @ColumnInfo(name = "recipe_id") val recipeId: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "favorite") val isFavorite: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (recipeId != other.recipeId) return false
        if (title != other.title) return false
        if (isFavorite != other.isFavorite) return false
        return true
    }

    override fun hashCode(): Int {
        var result = recipeId.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        return result
    }

    companion object {
        val recipe1 = Recipe("1af01c", "Cakespy: Cadbury Creme Deviled Eggs", false)
        val recipe2 = Recipe("1cea66", "Poached Eggs in Tomato Sauce with Chickpeas and Feta", false)

        val recipe1_favored = recipe1.copy(isFavorite = true)

        var mRecipes = listOf(recipe1, recipe2)
        var mFavorites = listOf(recipe1_favored)
    }
}
