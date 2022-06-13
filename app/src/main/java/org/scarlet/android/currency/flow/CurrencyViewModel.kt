package org.scarlet.android.currency.flow

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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BigDecimal.ZERO
    )

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        _currencySymbol.value = currencySymbolMap[currency]!!
        _currency.value = currency
        _amount.value = amount
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class CurrencyViewModelFactory(
    private val currencyApi: CurrencyApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(CurrencyViewModel::class.java))
            throw IllegalArgumentException("No such viewmodel")
        return CurrencyViewModel(currencyApi) as T
    }
}