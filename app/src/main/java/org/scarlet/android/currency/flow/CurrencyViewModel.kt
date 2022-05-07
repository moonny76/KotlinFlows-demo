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

    private val currencySymbolMap = mutableMapOf(
        "dollar" to "$",
        "pound" to "£",
        "yen" to "¥",
    )

    private val _currencySymbol = MutableStateFlow("$")
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _exchangeRate = MutableStateFlow(0.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    private val _amount = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val totalAmount: Flow<BigDecimal> = _amount.combine(exchangeRate) { amount, rate ->
        amount * rate.toBigDecimal()
    }

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        _currencySymbol.value = currencySymbolMap[currency]!!
        viewModelScope.launch {
            _exchangeRate.value = currencyApi.getExchangeRate(currency)
            _amount.value = amount
        }
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