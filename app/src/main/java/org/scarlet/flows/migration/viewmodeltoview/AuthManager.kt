package org.scarlet.flows.migration.viewmodeltoview

import org.scarlet.flows.model.User
import kotlinx.coroutines.flow.Flow

interface AuthManager {
    fun observeUser(): Flow<User>
}
