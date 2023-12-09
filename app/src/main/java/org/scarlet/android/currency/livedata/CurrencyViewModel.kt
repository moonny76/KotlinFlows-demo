package org.scarlet.android.currency.livedata

import androidx.lifecycle.*
import org.scarlet.android.currency.CurrencyApi
import kotlinx.coroutines.*
import java.math.BigDecimal
import kotlin.collections.mutableMapOf

class CurrencyViewModel(
    private val currencyApi: CurrencyApi
) : ViewModel() {

    private val currencySymbolMap: MutableMap<String, String> = mutableMapOf(
        "dollar" to "$",
        "pound" to "£",
        "yen" to "¥",
    )

    private val _currencySymbol = MutableLiveData<String>(currencySymbolMap["dollar"])
    val currencySymbol: LiveData<String> = _currencySymbol

    private val _currency = MutableLiveData("dollar")

    val exchangeRate: LiveData<Double> = _currency.switchMap { currency ->
        liveData {
            while (true) {
                val rate = currencyApi.getExchangeRate(currency)
                emit(rate)
                delay(1000)
            }
        }
    }

    private val _amountEntered = MutableLiveData<BigDecimal>()
    val totalAmount: LiveData<BigDecimal> = _amountEntered.switchMap { amount ->
        exchangeRate.map { rate ->
            amount * rate.toBigDecimal()
        }
    }

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        _currencySymbol.value = currencySymbolMap[currency]
        _currency.value = currency
        _amountEntered.value = amount
    }
}

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