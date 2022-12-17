@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.scarlet.flows.migration.callbacks.stream

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.scarlet.flows.migration.callbacks.stream.FakeLocationService.Companion.Mode
import org.scarlet.util.testDispatcher
import java.io.IOException

class LocationServiceTest {

    private lateinit var locationService: LocationService

    @MockK(relaxUnitFun = true)
    lateinit var callback: LocationCallback

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test multi-shot callback - success`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(testDispatcher)
        val request = LocationRequest("seoul", 5_000L)

        // Act (When)
        locationService.requestLocationUpdates(request, callback)

        advanceTimeBy(request.timeMs); runCurrent()

        locationService.removeLocationUpdates(callback)

        // Assert (Then)
        verifySequence {
            callback.onLocation(FakeLocationService.testLocations[0])
            callback.onLocation(FakeLocationService.testLocations[1])
        }
    }

    @Test
    fun `test multi-shot callback - failure`() = runTest {
        locationService = FakeLocationService(testDispatcher, Mode.Fail)
        val request = LocationRequest("me", 5_000L)

        // Act (When)
        locationService.requestLocationUpdates(request, callback)

        advanceTimeBy(request.timeMs); runCurrent()

        locationService.removeLocationUpdates(callback)

        // Assert (Then)
        verify { callback.onFailure(ofType(IOException::class)) }
        verify { callback.onFailure(match {it.message == "Failed"}) }
    }

    @Test
    fun `test callbackFlow - success`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(testDispatcher)
        val request = LocationRequest("seoul", 1_000L)

        // Act (When)
        val flow = locationService.requestLocationUpdatesFlow(request)

        flow.test {
            assertThat(awaitItem()).isEqualTo(FakeLocationService.testLocations[0])
            assertThat(awaitItem()).isEqualTo(FakeLocationService.testLocations[1])
        }
    }

    @Test
    fun `test callbackFlow - failure`() = runTest {
        // Arrange (Given)
        locationService = FakeLocationService(testDispatcher, Mode.Fail)

        // Act (When)
        val request = LocationRequest("seoul", 1_000L)

        // Act (When)
        locationService.requestLocationUpdatesFlow(request).test {
            // Assert (Then)
            assertThat(awaitItem()).isNull()
        }
    }
}