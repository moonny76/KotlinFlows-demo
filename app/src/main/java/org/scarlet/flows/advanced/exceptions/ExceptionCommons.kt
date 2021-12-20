package org.scarlet.flows.advanced.exceptions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException

internal fun dataFlow(): Flow<Int> {
    return flow {
        for (i in 1..3) {
            emit(i)
        }
    }
}

internal fun dataFlowThrow() = flow {
    emit(1)
    emit(2)
    throw RuntimeException("oops")
}

internal fun showErrorMessage(ex: Throwable) {
    println(ex)
}

internal fun updateUI(value: Int) {
    println("updateUI $value")
}
