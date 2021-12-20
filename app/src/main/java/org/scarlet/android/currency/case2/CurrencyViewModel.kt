package org.scarlet.android.currency.case2

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

    private val currencySymbolMap = mutableMapOf(
        "dollar" to "$",
        "pound" to "£",
        "yen" to "¥",
    )

    //region Version 1: LiveData
//    private val _currentCurrency = MutableLiveData("dollar")
//
//    fun onCurrency(currency: String) {
//        _currentCurrency.value = currency
//    }
//
//    private val _currencySymbol = MutableLiveData<String>()
//    val currencySymbol: LiveData<String> = _currentCurrency.map { currency ->
//        currencySymbolMap[currency]!!
//    }
//
//    val exchangeRate: LiveData<Double> = _currentCurrency.switchMap { currency ->
//        liveData {
//            emit(currencyApi.getExchangeRate(currency))
//        }
//    }
//
//    private val _amount = MutableLiveData<BigDecimal>()
//    val totalAmount: LiveData<BigDecimal> = _amount.switchMap { amount ->
//        exchangeRate.map { rate ->
//            amount * rate.toBigDecimal()
//        }
//    }
//
//    fun onOrderSubmit(amount: BigDecimal, currency: String) {
//        _currencySymbol.value = currencySymbolMap[currency]
//        viewModelScope.launch {
//            _amount.value = amount
//        }
//    }
    //endregion

    //region Version 2: StateFlow
//    private val _currentCurrency = MutableStateFlow("dollar")
//
//    fun onCurrency(currency: String) {
//        _currentCurrency.value = currency
//    }
//
//    val currencySymbol: Flow<String> = _currentCurrency.mapLatest { currency ->
//        currencySymbolMap[currency]!!
//    }
//
//    val exchangeRate: Flow<Double> = _currentCurrency.mapLatest { currency ->
//        currencyApi.getExchangeRate(currency)
//    }
//
//    private val _amount = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
//    val totalAmount: Flow<BigDecimal> = _amount.combine(exchangeRate) { amount, rate ->
//        amount * rate.toBigDecimal()
//    }
//
//    fun onOrderSubmit(amount: BigDecimal, currency: String) {
//        _currentCurrency.value = currency
//        _amount.value = amount
//    }
    //endregion

    // Shouldn't be StateFlow why?
    private val _currentCurrency = MutableSharedFlow<String>(replay = 1)

    fun onCurrency(currency: String) {
        viewModelScope.launch {
            _currentCurrency.emit(currency)
        }
    }

    val currencySymbol: Flow<String> = _currentCurrency.mapLatest { currency ->
        currencySymbolMap[currency]!!
    }

    val exchangeRate: SharedFlow<Double> = _currentCurrency.mapLatest { currency ->
        Log.d(TAG, "exchange rate: currency before = $currency")
//        try {
            val rate = currencyApi.getExchangeRate(currency)
            Log.d(TAG, "exchange rate: currency after = $currency, rate = $rate")
            rate
//        } catch (ex: Exception) {
//            Log.d(TAG, "catch: ${ex.javaClass.simpleName}")
//            throw ex
//        }
    }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily
        )

    private val _amount = MutableSharedFlow<BigDecimal>()
    val totalAmount = _amount.combine(exchangeRate) { amount, rate ->
        Log.d(TAG, "totalAmount: amount = $amount, rate = $rate")
        amount * rate.toBigDecimal()
    }

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        viewModelScope.launch {
            Log.d(TAG, "onOrderSubmit: emit _currency")
            _currentCurrency.emit(currency)
            Log.d(TAG, "onOrderSubmit: emit amount")
            _amount.emit(amount)
        }
    }

    companion object {
        const val TAG = "Currency"
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