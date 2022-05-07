package org.scarlet.android.currency.livedata

import androidx.lifecycle.*
import org.scarlet.android.currency.CurrencyApi
import kotlinx.coroutines.*
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

    private val _currencySymbol = MutableLiveData<String>()
    val currencySymbol: LiveData<String> = _currencySymbol

    private val _exchangeRate = MutableLiveData<Double>()
    val exchangeRate: LiveData<Double> = _exchangeRate

    private val _amount = MutableLiveData<BigDecimal>()
    val totalAmount: LiveData<BigDecimal> = _amount.switchMap { amount ->
        exchangeRate.map { rate ->
            amount * rate.toBigDecimal()
        }
    }

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        _currencySymbol.value = currencySymbolMap[currency]
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