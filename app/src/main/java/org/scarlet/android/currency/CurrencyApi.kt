package org.scarlet.android.currency

interface CurrencyApi {
    suspend fun getExchangeRate(currency: String): Double
}