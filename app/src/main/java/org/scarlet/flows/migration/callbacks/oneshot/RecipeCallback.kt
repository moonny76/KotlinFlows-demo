package org.scarlet.flows.migration.callbacks.oneshot

import org.scarlet.flows.model.Recipe
import org.scarlet.util.Resource

interface RecipeCallback {
    fun onReceive(response: Resource<Recipe>)
}