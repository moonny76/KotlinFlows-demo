package org.scarlet.flows.basics

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.math.BigInteger

@ExperimentalCoroutinesApi
object DataSource {

    // Finite flow
    fun counter() = flow {
        repeat(10) {
            emit(it)
        }
    }

    // Infinite flow
    fun fibo(): Flow<BigInteger> = flow {
        var x = BigInteger.ZERO
        var y = BigInteger.ONE
        while (true) {
            emit(x)
            x = y.also {
                y += x
            }
        }
    }

    private val _tokens = MutableStateFlow(0)
    val tokens: StateFlow<Int> = _tokens

    suspend fun genToken() {
        while (true) {
            delay(200)
            _tokens.value += 1
        }
    }
}
