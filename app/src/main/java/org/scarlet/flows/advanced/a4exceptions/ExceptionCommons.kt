package org.scarlet.flows.advanced.a4exceptions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.scarlet.util.log
import java.lang.RuntimeException

fun dataFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        emit(i)
    }
}

fun dataFlowThrow() = flow {
    emit(1)
    throw RuntimeException("oops")
}

fun showErrorMessage(ex: Throwable) {
    log("showErrorMessage: Exception caught: $ex")
}

fun updateUI(value: Int) {
    log("updateUI $value")
}
