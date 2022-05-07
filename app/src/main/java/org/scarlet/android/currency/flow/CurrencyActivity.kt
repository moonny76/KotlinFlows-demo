package org.scarlet.android.currency.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.scarlet.R
import org.scarlet.android.currency.FakeCurrencyApi
import org.scarlet.util.hideKeyboard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.math.BigDecimal
import java.text.DecimalFormat

@ExperimentalCoroutinesApi
class CurrencyActivity : AppCompatActivity() {
    private lateinit var detailsSymbolTargetCurrency: TextView
    private lateinit var detailsExchangeRate: TextView
    private lateinit var amountEntered: EditText
    private lateinit var detailsTotalAmount: TextView
    private lateinit var detailsAmount: TextView

    private lateinit var orderButton: Button
    private var currentCurrency: String? = "dollar"

    val viewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory(FakeCurrencyApi())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_main)

        getViews()

        orderButton.setOnClickListener {
            amountEntered.text.toString().toBigDecimalOrNull()?.let { amount ->
                hideKeyboard()
                detailsAmount.text = format(amount)
                viewModel.onOrderSubmit(amount, currentCurrency!!)
            } ?: showEmptyAmountWarning()
        }

        subscribeObservers()
    }

    private fun showEmptyAmountWarning() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Enter amount to buy",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getViews() {
        amountEntered = findViewById(R.id.amount_entered)
        detailsSymbolTargetCurrency = findViewById(R.id.details_symbol_target_currency)
        detailsExchangeRate = findViewById(R.id.details_exchange_rate)
        detailsTotalAmount = findViewById(R.id.details_total_amount)
        detailsAmount = findViewById(R.id.details_amount)
        orderButton = findViewById(R.id.orderButton)
    }

    private fun subscribeObservers() {

        lifecycleScope.launchWhenStarted {
            viewModel.currencySymbol.collect { symbol ->
                detailsSymbolTargetCurrency.text = symbol
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.exchangeRate.collect { rate ->
                detailsExchangeRate.text = rate.toString()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.totalAmount.collect { total ->
                detailsTotalAmount.text = format(total)
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
    }

}