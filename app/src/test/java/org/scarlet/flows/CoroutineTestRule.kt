package org.scarlet.flows

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.scarlet.util.DispatchersProvider
import org.scarlet.util.log

@ExperimentalCoroutinesApi
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher(){

    val testDispatchersProvider = object : DispatchersProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    override fun starting(description: Description?) {
        super.starting(description)

        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)

        Dispatchers.resetMain()
    }
}