package org.scarlet.flows.migration.callbacks.stream

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface LocationService {
    @Deprecated(
        "Obsolete API - use requestLocationFlow instead",
        replaceWith = ReplaceWith("requestLocationflow(location, timeMs)")
    )
    fun requestLocationUpdates(request: LocationRequest, callback: LocationCallback)
    fun removeLocationUpdates(callback: LocationCallback)
}

data class LocationRequest(
    val location: String,
    val timeMs: Long
)

@ExperimentalCoroutinesApi
fun LocationService.requestLocationUpdatesFlow(request: LocationRequest): Flow<Location?> = TODO()
