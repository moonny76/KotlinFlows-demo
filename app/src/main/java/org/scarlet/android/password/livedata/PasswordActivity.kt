package org.scarlet.android.password.livedata

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
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

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.flowSource.setOnClickListener {
            viewModel.increment()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.loginUiState.observe(this) { state ->
            state?.let {
                handleState(state)
            }
        }

        viewModel.counter.observe(this) { counter ->
            counter?.let {
                binding.counts.text = counter.toString()
            }
        }
    }

    private fun handleState(state: LoginUiState) {
        when (state) {
            is LoginUiState.Success -> {
                Snackbar.make(binding.root, "Successfully logged in", Snackbar.LENGTH_LONG).show()
                binding.progressBar.isVisible = false
            }

            is LoginUiState.Error -> {
                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
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