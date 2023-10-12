package org.scarlet.android.collect

import android.os.Bundle
import android.util.Log
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
    val viewModel by lazy { SafeCollectViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_main)

        Log.w(TAG, "onCreate: ")

//        lifecycleScope.launch {
//            Log.e(TAG, "[launch] launch started")
//            viewModel.numbers.collect {
//                Log.e(TAG, "[launch] collecting numbers: $it")
//            }
//        }.invokeOnCompletion {
//            Log.e(TAG, "[launch] completed: $it")
//        }

//        lifecycleScope.launchWhenCreated {
//            Log.v(TAG, "[launchWhenCreated] launchWhenCreated started")
//            viewModel.numbers.collect {
//                Log.v(TAG, "[launchWhenCreated] collecting numbers: $it")
//            }
//        }.invokeOnCompletion {
//            Log.v(TAG, "[launchWhenCreated] completed: $it")
//        }

//        lifecycleScope.launchWhenStarted {
//            Log.d(TAG, "\t[launchWhenStarted] launchWhenStarted started")
//            viewModel.numbers.collect {
//                Log.d(TAG, "\t[launchWhenStarted] collecting numbers: $it")
//            }
//        }.invokeOnCompletion {
//            Log.d(TAG, "\t[launchWhenStarted] completed: $it")
//        }
//
//        lifecycleScope.launchWhenResumed {
//            Log.d(TAG, "\t\t[launchWhenResumed] launchWhenResumed started")
//            viewModel.numbers.collect {
//                Log.d(TAG, "\t\t[launchWhenResumed] collecting numbers: $it")
//            }
//        }.invokeOnCompletion {
//            Log.d(TAG, "\t\t[launchWhenResumed] completed: $it")
//        }

        lifecycleScope.launch {
            Log.i(TAG, "[repeatOnLifeCycle] launch for repeatOnLifecycle")
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                Log.i(TAG, "[repeatOnLifeCycle] repeatOnLifeCycle body started")
                coroutineContext.job.invokeOnCompletion {
                    Log.v(TAG, "[repeatOnLifeCycle] repeatOnLifeCycle job completed: $it")
                }
//                Log.i(TAG, "[repeatOnLifeCycle] coroutineContext = ${currentCoroutineContext()}")
                viewModel.numbers.collect {
                    Log.i(TAG, "[repeatOnLifeCycle] collecting numbers: $it")
                }
            }
            Log.e(TAG, "[repeatOnLifeCycle] Printed only when `lifecycle` is destroyed ...")
        }.invokeOnCompletion {
            Log.i(TAG, "[repeatOnLifeCycle] completed: $it")
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
