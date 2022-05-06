package org.scarlet.flows.advanced.a4exceptions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.scarlet.util.log
import java.lang.RuntimeException

internal fun dataFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        emit(i)
    }
}

internal fun dataFlowThrow() = flow {
    emit(1)
    emit(2)
    throw RuntimeException("oops")
}

internal fun showErrorMessage(ex: Throwable) {
    log(ex)
}

internal fun updateUI(value: Int) {
    log("updateUI $value")
}
