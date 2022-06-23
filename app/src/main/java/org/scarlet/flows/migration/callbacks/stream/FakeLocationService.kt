package org.scarlet.flows.migration.callbacks.stream

import kotlinx.coroutines.*
import java.io.IOException

@DelicateCoroutinesApi
class FakeLocationService(
    val dispatcher: CoroutineDispatcher,
    var mode: Mode = Mode.Success
) : LocationService {
    private var callbacks: LocationCallback? = null
    private var job: Job? = null

    @Deprecated(
        message = "Obsolete API - use requestLocationFlow instead",
        replaceWith = ReplaceWith("requestLocationFlow(location, timeMs)")
    )
    override fun requestLocationUpdates(request: LocationRequest, callback: LocationCallback) {
        this.callbacks = callback
        var next = 0

        when (mode) {
            Mode.Success -> {
                job = GlobalScope.launch(dispatcher) {
                    while (callbacks != null) {
                        callbacks?.onLocation(testLocations[next++ % 2])
                        delay(request.timeMs)
                    }
                }
            }
            Mode.Fail -> {
                job = GlobalScope.launch(dispatcher) {
                    delay(request.timeMs)
                    callbacks?.onFailure(IOException("Failed"))
                }
            }
        }

    }

    override fun removeLocationUpdates(callback: LocationCallback) {
        job?.cancel()
        this.callbacks = null
    }

    companion object {
        enum class Mode { Success, Fail }

        val testLocations = listOf(Location(36.5, 125.7), Location(37.5, 126.8))
    }

}