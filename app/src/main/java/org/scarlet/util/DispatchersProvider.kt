package org.scarlet.util

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DefaultDispatchersProvider : DispatchersProvider {
    override val main: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main
    override val io: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
    override val default: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
}