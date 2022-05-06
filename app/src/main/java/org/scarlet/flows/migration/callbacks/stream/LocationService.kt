package org.scarlet.flows.migration.callbacks.stream

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.scarlet.util.log

interface LocationService {
    @Deprecated(
        "Obsolete API - use requestLocationFlow instead",
        replaceWith = ReplaceWith("requestLocationFlow(location, timeMs)")
    )
    fun requestLocationUpdates(request: LocationRequest, callback: LocationCallback)
    fun removeLocationUpdates(callback: LocationCallback)
}

data class LocationRequest(
    val location: String,
    val timeMs: Long
)

//fun LocationService.requestLocationUpdatesFlow(request: LocationRequest): Flow<Location?> = TODO()

fun LocationService.requestLocationUpdatesFlow(request: LocationRequest): Flow<Location?> =
    callbackFlow {
        val callback: LocationCallback = object : LocationCallback {
            override fun onLocation(location: Location) {
                trySend(location)
            }

            override fun onFailure(ex: Throwable) {
                trySend(null)
            }
        }

        requestLocationUpdates(request, callback)

        awaitClose {
            removeLocationUpdates(callback)
            log("awaitClose")
        }
    }
