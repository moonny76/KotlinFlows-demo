package org.scarlet.android.currency

import kotlinx.coroutines.delay
import java.lang.IllegalArgumentException
import kotlin.random.Random

interface CurrencyApi {
    suspend fun getExchangeRate(currency: String): Double
}

class FakeCurrencyApi : CurrencyApi {
    private val dollars = listOf(
        1182.00,
        1181.50,
        1178.43,
        1176.24,
        1173.37,
    )

    private val pounds = listOf(
        1605.14,
        1600.53,
        1590.89,
        1625.23,
        1598.55,
    )

    private val yens = listOf(
        10.69,
        10.26,
        10.16,
        10.54,
        10.33,
    )


    override suspend fun getExchangeRate(currency: String): Double {
        delay(1_000) // fake network delay

        return when (currency) {
            "dollar" -> dollars[Random.nextInt(0, 5)]
            "pound" -> pounds[Random.nextInt(0, 5)]
            "yen" -> yens[Random.nextInt(0, 5)]
            else -> throw IllegalArgumentException("No such currency")
        }
    }
}