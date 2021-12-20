package org.scarlet.flows.migration.callbacks.stream

interface LocationCallback {
    fun onLocation(location: Location)
    fun onFailure(ex: Throwable)
}

data class Location(val x: Double, val y: Double)