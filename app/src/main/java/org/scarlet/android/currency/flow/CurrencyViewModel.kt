package org.scarlet.android.currency.flow

import android.util.Log
import androidx.lifecycle.*
import org.scarlet.android.currency.CurrencyApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import kotlin.collections.mutableMapOf

@ExperimentalCoroutinesApi
class CurrencyViewModel(
    private val currencyApi: CurrencyApi
) : ViewModel() {

    private val currencySymbolMap: MutableMap<String, String> = mutableMapOf(
        "dollar" to "$",
        "pound" to "£",
        "yen" to "¥",
    )

    private val _currencySymbol = MutableStateFlow(currencySymbolMap["dollar"]!!)
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _currency = MutableStateFlow("dollar")

    val exchangeRate: StateFlow<Double> = _currency.flatMapLatest { currency ->
        flow {
            while (true) {
                val rate = currencyApi.getExchangeRate(currency)
                emit(rate)
                Log.d(TAG, "exchange rate = $rate")
                delay(1000)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    private val _amount = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val totalAmount: StateFlow<BigDecimal> = _amount.combine(exchangeRate) { amount, rate ->
        amount * rate.toBigDecimal()
    }
        .onEach { Log.i(TAG, "total amount = $it") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BigDecimal.ZERO
        )

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        _currencySymbol.value = currencySymbolMap[currency]!!
        _currency.value = currency
        _amount.value = amount
    }

    companion object {
        private const val TAG = "Currency"
    }
}

