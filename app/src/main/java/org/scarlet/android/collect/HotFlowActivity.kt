package org.scarlet.android.collect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.scarlet.R

class HotFlowActivity : AppCompatActivity() {
    private val viewModel by lazy { HotFlowViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_main)

        viewModel.startFlow(HotFlowViewModel.Companion.FlowKind.FLOW3)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.flow.collect {
                    Log.d(TAG, "View: collected = $it")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.w(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.w(TAG, "onStop: ")
    }

    override fun onPause() {
        super.onPause()
        Log.w(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.w(TAG, "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy:")
    }


    companion object {
        private const val TAG = "Producer"
    }
}