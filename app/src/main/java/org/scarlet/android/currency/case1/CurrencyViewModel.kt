package org.scarlet.android.currency.case1

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

//    private val _currencySymbol = MutableLiveData<String>()
//    val currencySymbol: LiveData<String> = _currencySymbol
//
//    private val _exchangeRate = MutableLiveData<Double>()
//    val exchangeRate: LiveData<Double> = _exchangeRate
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
//            _exchangeRate.value = currencyApi.getExchangeRate(currency)
//            _amount.value = amount
//        }
//    }

    // Version 2.
//    private val _currencySymbol = MutableStateFlow("$")
//    val currencySymbol: StateFlow<String> = _currencySymbol
//
//    private val _exchangeRate = MutableStateFlow(0.0)
//    val exchangeRate: StateFlow<Double> = _exchangeRate
//
//    private val _amount = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
//    val totalAmount: Flow<BigDecimal> = _amount.combine(exchangeRate) { amount, rate ->
//        amount * rate.toBigDecimal()
//    }
//
//    fun onOrderSubmit(amount: BigDecimal, currency: String) {
//        _currencySymbol.value = currencySymbolMap[currency]!!
//        viewModelScope.launch {
//            _exchangeRate.value = currencyApi.getExchangeRate(currency)
//            _amount.value = amount
//        }
//    }

    // Shouldn't be StateFlow why?
    private val _currencySymbol = MutableSharedFlow<String>(replay = 0).apply {
        tryEmit(currencySymbolMap["dollar"]!!)
        distinctUntilChanged()
    }
    val currencySymbol: SharedFlow<String> = _currencySymbol

    private val _exchangeRate = MutableSharedFlow<Double>(replay = 0).apply {
        tryEmit(0.0)
        distinctUntilChanged()
    }
    val exchangeRate: SharedFlow<Double> = _exchangeRate

//    val exchangeRate: SharedFlow<Double> = _currencySymbol.mapLatest { currency ->
//        Log.d(TAG, "exchange rate: currency before = $currency")
//        try {
//            val rate = currencyApi.getExchangeRate(currency)
//            Log.d(TAG, "exchange rate: currency after = $currency, rate = $rate")
//            rate
//        } catch (ex: Exception) {
//            Log.d(TAG, "catch: ${ex.javaClass.simpleName}")
//            throw ex
//        }
//    }
//        .catch { ex ->
//            Log.d(TAG, "catch: ${ex.javaClass.simpleName}")
//            if (ex is CancellationException)
//                throw ex
//        }
//        .shareIn(
//            scope = viewModelScope,
//            replay = 1,
//            started = SharingStarted.Lazily
//        )

    private val _amount = MutableSharedFlow<BigDecimal>(replay = 0)
    val totalAmount = _amount.combine(exchangeRate) { amount, rate ->
        Log.d(TAG, "totalAmount: amount = $amount, rate = $rate")
        amount * rate.toBigDecimal()
    }

    fun onOrderSubmit(amount: BigDecimal, currency: String) {
        viewModelScope.launch {
            Log.d(TAG, "onOrderSubmit: emit _currency")
            _currencySymbol.emit(currencySymbolMap[currency]!!)
            _exchangeRate.emit(currencyApi.getExchangeRate(currency))
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