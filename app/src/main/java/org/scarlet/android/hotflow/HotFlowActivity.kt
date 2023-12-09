package org.scarlet.android.hotflow

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.scarlet.android.hotflow.HotFlowViewModel.Companion.FlowKind
import org.scarlet.databinding.ActivityHotflowMainBinding


@ExperimentalCoroutinesApi
class HotFlowActivity : AppCompatActivity() {
    private val viewModel: HotFlowViewModel by viewModels()
    private lateinit var binding: ActivityHotflowMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotflowMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var job: Job? = null
        binding.flowButton1.setOnClickListener {
            job = doIt(FlowKind.FLOW1, job)
        }

        binding.flowButton2.setOnClickListener {
            job = doIt(FlowKind.FLOW2, job)
        }

        binding.flowButton3.setOnClickListener {
            job = doIt(FlowKind.FLOW3, job)
        }
    }

    private fun doIt(kind: FlowKind, job: Job?): Job {
        job?.cancel()
        viewModel.startFlow(kind)
        return startCollection()
    }

    private fun startCollection(): Job =
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                coroutineContext.job.apply {
                    invokeOnCompletion {
                        Log.w(TAG, "View: repeatOnLifeCycle, isCancelled = $isCancelled")
                    }
                }

                viewModel.flow.collect {
                    Log.d(TAG, "View: collected = $it")
                }
            }
        }.apply {
            invokeOnCompletion {
                Log.w(TAG, "View: lifecycleScope, isCancelled = $isCancelled")
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