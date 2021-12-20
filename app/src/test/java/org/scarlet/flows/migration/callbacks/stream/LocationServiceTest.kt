package org.scarlet.flows.migration.callbacks.stream

import app.cash.turbine.test
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.scarlet.flows.CoroutineTestRule
import java.io.IOException

@DelicateCoroutinesApi
class LocationServiceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = CoroutineTestRule()

    @MockK
    lateinit var locationService: LocationService

    val testLocations = listOf(Location(36.5, 125.7), Location(37.5, 126.8))

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test multi-shot callback - success`() = rule.runBlockingTest {
        // Arrange (Given)
        val slot = slot<LocationCallback>()
        every {
            locationService.requestLocationUpdates(any(), capture(slot))
        } coAnswers {
            launch(rule.testDispatcher) {
                testLocations.forEach {
                    println(currentCoroutineContext())
                    slot.captured.onLocation(it)
                    delay(5000)
                    println(currentTime)
                }
            }
        }

        val request = LocationRequest("me", 5_000L)
        val callback = object : LocationCallback {
            override fun onLocation(location: Location) {
                // Assert (Then)
                println(location)
            }

            override fun onFailure(ex: Throwable) {
                fail("Should not be called")
            }
        }

        // Act (When)
        locationService.requestLocationUpdates(request, callback)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test multi-shot callback - failure`() = rule.runBlockingTest {
        // Arrange (Given)
        val slot = slot<LocationCallback>()
        every {
            locationService.requestLocationUpdates(any(), capture(slot))
        } coAnswers {
            launch(rule.testDispatcher) {
                slot.captured.onLocation(testLocations[0])
                delay(1_000)
                slot.captured.onFailure(IOException("Oops"))
            }
        }
        justRun { locationService.removeLocationUpdates(ofType(LocationCallback::class)) }

        val request = LocationRequest("seoul", 1_000L)
        val callback = object : LocationCallback {
            override fun onLocation(location: Location) {
                println(location)
            }

            override fun onFailure(ex: Throwable) {
                // Assert (Then)
                println(ex)
                locationService.removeLocationUpdates(this)
            }
        }

        // Act (When)
        locationService.requestLocationUpdates(request, callback)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test callbackFlow - success`() = rule.runBlockingTest {
        // Arrange (Given)
        val slot = slot<LocationCallback>()
        every {
            locationService.requestLocationUpdates(any(), capture(slot))
        } coAnswers {
            launch(rule.testDispatcher) {
                testLocations.forEach {
                    slot.captured.onLocation(it)
                    delay(1000)
                }
            }
        }
        justRun { locationService.removeLocationUpdates(ofType(LocationCallback::class)) }

        val request = LocationRequest("me", 1_000L)

        // Act (When)
        locationService.requestLocationUpdatesFlow(request).test {
            println(awaitItem())
            println(awaitItem())
        }

        verify {
            locationService.removeLocationUpdates(ofType(LocationCallback::class))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test callbackFlow - failure`() = rule.runBlockingTest {
        // Arrange (Given)
        val slot = slot<LocationCallback>()
        every {
            locationService.requestLocationUpdates(any(), capture(slot))
        } coAnswers {
            launch(rule.testDispatcher) {
                slot.captured.onLocation(testLocations[0])
                delay(1000)
                slot.captured.onFailure(IOException("Oops"))
            }
        }
        justRun { locationService.removeLocationUpdates(ofType(LocationCallback::class)) }

        val request = LocationRequest("me", 1_000L)

        // Act (When)
        locationService.requestLocationUpdatesFlow(request).test {
            println(awaitItem())
            println(awaitItem())
        }

        verify {
            locationService.removeLocationUpdates(ofType(LocationCallback::class))
        }
    }
}