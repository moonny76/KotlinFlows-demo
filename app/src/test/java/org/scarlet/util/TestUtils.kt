package org.scarlet.util

import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor

val CoroutineScope.testDispatcher: CoroutineDispatcher
    get() = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher

