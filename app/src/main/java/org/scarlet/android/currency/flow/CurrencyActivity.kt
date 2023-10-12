package org.scarlet.android.currency.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.scarlet.R
import org.scarlet.android.currency.FakeCurrencyApi
import org.scarlet.util.hideKeyboard
import java.math.BigDecimal
import java.text.DecimalFormat

@ExperimentalCoroutinesApi
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
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                viewModel.currencySymbol.collect { symbol ->
//                    currencySymbol.text = symbol
//                }
//            }
//        }

        repeatOn(viewModel.currencySymbol) { symbol ->
            currencySymbol.text = symbol
        }

        repeatOn(viewModel.exchangeRate) { rate ->
            exchangeRate.text = rate.toString()
        }

        repeatOn(viewModel.totalAmount) { total ->
            totalAmount.text = format(total)
        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                viewModel.exchangeRate.collect { rate ->
//                    exchangeRate.text = rate.toString()
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                viewModel.totalAmount.collect { total ->
//                    totalAmount.text = format(total)
//                }
//            }
//        }
    }

    private fun <T> repeatOn(
        flow: Flow<T>,
        state: Lifecycle.State = Lifecycle.State.RESUMED,
        action: (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                flow.collect(action)
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