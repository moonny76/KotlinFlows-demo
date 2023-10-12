package org.scarlet.android.currency.livedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.scarlet.android.currency.CurrencyApi

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