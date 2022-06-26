package org.scarlet.android.collect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.scarlet.flows.model.Recipe
import org.scarlet.flows.model.Recipe.Companion.mRecipes

class SafeCollectViewModel : ViewModel() {
    private val _recipes = MutableSharedFlow<List<Recipe>>(1)
    val recipes = _recipes.asSharedFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                _recipes.emit(mRecipes)
                delay(FAKE_NETWORK_DELAY)
            }
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        delay(FAKE_NETWORK_DELAY)
        return mRecipes
    }

    companion object {
        const val FAKE_NETWORK_DELAY = 5_000L
    }
}