package org.scarlet.android.password.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.scarlet.android.password.flow.PasswordViewModel.*
import org.scarlet.databinding.ActivityPasswordMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
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
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        /**/

        binding.flowSource.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.eventChannel.send(Unit)
            }
        }

        subscribeObservers()

    }

    private fun subscribeObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.loginUiState.collect { state ->
                handleState(state)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.countFlow.collect { count ->
                binding.counts.text = count.toString()
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

    companion object {
        const val TAG = "Password"
    }
}