package org.scarlet.flows.migration.callbacks.stream

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.scarlet.flows.CoroutineTestRule
import java.io.IOException

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
class LocationServiceTest {

    @get:Rule
    val rule = CoroutineTestRule()

    lateinit var locationService: LocationService

    @MockK(relaxed = true)
    lateinit var callback: LocationCallback

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @ExperimentalStdlibApi
    @Test
    fun `test multi-shot callback - success`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(coroutineContext[CoroutineDispatcher.Key]!!)
        val request = LocationRequest("me", 5_000L)

        // Act (When)
        locationService.requestLocationUpdates(request, callback)

        advanceTimeBy(5_000L); runCurrent()

        locationService.removeLocationUpdates(callback)

        // Assert (Then)
        verify { callback.onLocation(FakeLocationService.testLocations[0]) }
        verify { callback.onLocation(FakeLocationService.testLocations[1]) }
    }

    @ExperimentalStdlibApi
    @Test
    fun `test multi-shot callback - failure`() = runTest {
        locationService = FakeLocationService(coroutineContext[CoroutineDispatcher.Key]!!,
            FakeLocationService.Companion.Mode.Fail
        )
        val request = LocationRequest("me", 5_000L)

        // Act (When)
        locationService.requestLocationUpdates(request, callback)

        advanceTimeBy(5_000L); runCurrent()

        locationService.removeLocationUpdates(callback)

        // Assert (Then)
        verify { callback.onFailure(ofType(IOException::class)) }
        verify { callback.onFailure(match {it.message == "Failed"}) }
    }

    @ExperimentalStdlibApi
    @Test
    fun `test callbackFlow - success`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(coroutineContext[CoroutineDispatcher.Key]!!)
        val request = LocationRequest("me", 1_000L)

        // Act (When)

    }

    @ExperimentalStdlibApi
    @Test
    fun `test callbackFlow - failure`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(coroutineContext[CoroutineDispatcher.Key]!!,
            FakeLocationService.Companion.Mode.Fail)

        val request = LocationRequest("me", 1_000L)

        // Act (When)

    }
}