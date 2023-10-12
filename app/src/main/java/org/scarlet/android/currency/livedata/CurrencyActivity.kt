package org.scarlet.android.currency.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import org.scarlet.R
import org.scarlet.android.currency.FakeCurrencyApi
import org.scarlet.util.hideKeyboard
import java.math.BigDecimal
import java.text.DecimalFormat

class CurrencyActivity : AppCompatActivity() {
    private lateinit var currencySymbol: TextView
    private lateinit var exchangeRate: TextView
    private lateinit var amountEntered: EditText
    private lateinit var totalAmount: TextView
    private lateinit var formattedAmount: TextView

    private var currentCurrency: String = "dollar"

    val viewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory(FakeCurrencyApi())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_main)

        getViews()
        subscribeObservers()

        amountEntered.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    onOrderSubmit()
                    hideKeyboard()
                    true
                }

                else -> false
            }
        }
    }

    private fun onOrderSubmit() {
        amountEntered.text.toString().toBigDecimalOrNull()?.let { amount ->
            formattedAmount.text = format(amount)
            viewModel.onOrderSubmit(amount, currentCurrency)
        } ?: run {
            amountEntered.setText("0")
            viewModel.onOrderSubmit(BigDecimal.ZERO, currentCurrency)
        }
    }

    private fun getViews() {
        amountEntered = findViewById(R.id.amount_entered)
        currencySymbol = findViewById(R.id.currency_symbol)
        exchangeRate = findViewById(R.id.exchange_rate)
        totalAmount = findViewById(R.id.total_amount)
        formattedAmount = findViewById(R.id.formatted_amount)
    }

    private fun subscribeObservers() {
        viewModel.currencySymbol.observe(this) { symbol ->
            symbol?.let {
                currencySymbol.text = it
            }
        }

        viewModel.exchangeRate.observe(this) { rate ->
            rate?.let {
                exchangeRate.text = rate.toString()
            }
        }

        viewModel.totalAmount.observe(this) { total ->
            total?.let {
                totalAmount.text = format(total)
            }
        }

    }

    private fun format(total: BigDecimal): String {
        val format = DecimalFormat("###,###,###.##")
        return format.format(total)
    }

    fun onRadioButtonClicked(view: View) {
        currentCurrency = when (view.id) {
            R.id.radio_dollars -> "dollar"
            R.id.radio_pounds -> "pound"
            else -> "yen"
        }
        onOrderSubmit()
    }
}