package org.scarlet.android.collect

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import org.scarlet.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SafeCollectActivity : AppCompatActivity() {
    private val viewModel: SafeCollectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_main)

        Log.w(TAG, "onCreate: ")

        lifecycleScope.launch {
            Log.e(TAG, "[launch] launch started")
            viewModel.numbers.collect {
                Log.e(TAG, "[launch] collecting numbers: $it")
            }
        }.invokeOnCompletion {
            Log.e(TAG, "[launch] completed: $it")
        }

//        lifecycleScope.launchWhenCreated {
//        lifecycleScope.launchWhenStarted {
        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "[launchWhenResumed] launchWhenResumed started")
            viewModel.numbers.collect {
                Log.d(TAG, "[launchWhenResumed] collecting numbers: $it")
            }
        }.invokeOnCompletion {
            Log.d(TAG, "[launchWhenResumed] completed: $it")
        }

        lifecycleScope.launch {
            Log.v(TAG, "[repeatOnLifeCycle] launch for repeatOnLifecycle")

            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.e(TAG, "[repeatOnLifeCycle] repeatOnLifeCycle body started")
                coroutineContext.job.invokeOnCompletion {
                    Log.v(TAG, "[repeatOnLifeCycle] repeatOnLifeCycle job completed: $it")
                }

                viewModel.numbers.collect {
                    Log.v(TAG, "[repeatOnLifeCycle] collecting numbers: $it")
                }
            }

            Log.e(TAG, "[repeatOnLifeCycle] Printed only when `lifecycle` is destroyed ...")
        }.invokeOnCompletion {
            Log.v(TAG, "[repeatOnLifeCycle] completed: $it")
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy:")
    }

    companion object {
        const val TAG = "Flow"
    }
}
