package org.scarlet.android.password.flow

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.scarlet.databinding.ActivityPasswordMainBinding
import org.scarlet.android.password.LoginUiState

class PasswordActivity : AppCompatActivity() {

    private var _binding: ActivityPasswordMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasswordMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Password", "onCreate: activity created ...")

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }

        /**/

        binding.flowSource.setOnClickListener {
            viewModel.increment()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {

        /**
         * TODO - handle `loginUiState`
         */
        lifecycleScope.launch {
            viewModel.loginUiState.collect { state ->
                state?.let {
                    handleState(state)
                }
            }
        }

        /**
         * TODO - subscribe to `counterFlow`
         */
        lifecycleScope.launch {
            viewModel.counterFlow.collect { counter ->
                counter?.let {
                    binding.counts.text = counter.toString()
                }
            }
        }

        /**
         * TODO - subscribe to `isError`
         */
        lifecycleScope.launch {
            viewModel.isError.collect { isError ->
                if (isError)
                    Snackbar.make(binding.root, "Wrong credentials", Snackbar.LENGTH_LONG).show()
                else
                    Snackbar.make(binding.root, "Successfully logged in", Snackbar.LENGTH_LONG)
                        .show()
            }
        }

    }

    private fun handleState(state: LoginUiState) {
        when (state) {
            is LoginUiState.Success -> {
//                Snackbar.make(binding.root, "Successfully logged in", Snackbar.LENGTH_LONG).show()
                binding.progressBar.isVisible = false
            }

            is LoginUiState.Error -> {
//                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                binding.progressBar.isVisible = false
            }

            is LoginUiState.Loading -> {
                binding.progressBar.isVisible = true
            }

            else -> Unit
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}