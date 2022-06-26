package org.scarlet.android.collect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import org.scarlet.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SafeCollectActivity : AppCompatActivity() {
    val viewModel by lazy { SafeCollectViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_main)

//        prepareFakeData()

        Log.w(TAG, "onCreate: massive launching started ...")

        lifecycleScope.launch {
            Log.e(TAG, "[launch] launch started")
            Log.e(TAG, "[launch] recipes = ${viewModel.searchRecipes("eggs")}")
//            viewModel.recipes.collect {
//                Log.e(TAG, "[launch] collecting recipes: $it")
//            }
        }.invokeOnCompletion {
            Log.e(TAG, "[launch] completed: $it")
        }

        lifecycleScope.launchWhenCreated {
            Log.v(TAG, "[launchWhenCreated] launchWhenCreated started")
            Log.v(TAG, "[launchWhenCreated] recipes = ${viewModel.searchRecipes("eggs")}")
//            viewModel.recipes.collect {
//                Log.v(TAG, "[launchWhenCreated] collecting recipes: $it")
//            }
        }.invokeOnCompletion {
            Log.v(TAG, "[launchWhenCreated] completed: $it")
        }

        lifecycleScope.launchWhenStarted {
            Log.d(TAG, "\t[launchWhenStarted] launchWhenStarted started")
            Log.d(TAG, "[launchWhenStarted] recipes = ${viewModel.searchRecipes("eggs")}")
//            viewModel.recipes.collect {
//                Log.d(TAG, "\t[launchWhenStarted] collecting recipes: $it")
//            }
        }.invokeOnCompletion {
            Log.d(TAG, "\t[launchWhenStarted] completed: $it")
        }

        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "\t\t[launchWhenResumed] launchWhenResumed started")
            Log.d(TAG, "[launchWhenResumed] recipes = ${viewModel.searchRecipes("eggs")}")
//            viewModel.recipes.collect {
//                Log.d(TAG, "\t\t[launchWhenResumed] collecting recipes: $it")
//            }
        }.invokeOnCompletion {
            Log.d(TAG, "\t\t[launchWhenResumed] completed: $it")
        }

        lifecycleScope.launch {
            Log.i(TAG, "[repeatOnLifeCycle] launch for repeatOnLifecycle")
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.i(TAG, "[repeatOnLifeCycle] repeatOnLifeCycle body started")
                Log.i(TAG, "[repeatOnLifeCycle] recipes = ${viewModel.searchRecipes("eggs")}")
//                viewModel.recipes.collect {
//                    Log.i(TAG, "[repeatOnLifeCycle] collecting recipes: $it")
//                }
            }
            Log.i(TAG, "[repeatOnLifeCycle] Printed only when `lifecycle` is destroyed ...")
        }.invokeOnCompletion {
            Log.i(TAG, "[repeatOnLifeCycle] completed: $it")
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
        const val TAG = "Flow"
    }
}
