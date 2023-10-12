package org.scarlet.android.currency.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.scarlet.android.currency.CurrencyApi

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