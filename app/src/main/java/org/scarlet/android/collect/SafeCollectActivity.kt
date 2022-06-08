package org.scarlet.android.collect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import org.scarlet.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.scarlet.flows.model.Recipe.Companion.mRecipes

@ExperimentalCoroutinesApi
class SafeCollectActivity : AppCompatActivity() {
    private val apiService = FakeRemoteDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_main)

        prepareFakeData()

        Log.d(TAG, "onCreate: massive launching started ...")

        lifecycleScope.launch {
            Log.d(TAG, "launch started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launch completed: $it")
        }

        lifecycleScope.launchWhenCreated {
            Log.d(TAG, "launchWhenCreated started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenCreated completed: $it")
        }

        lifecycleScope.launchWhenStarted {
            Log.d(TAG, "launchWhenStarted started")
        val recipes = apiService.searchRecipes("eggs")
        Log.d(TAG, "recipes = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenStarted completed: $it")
        }

        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "launchWhenResumed started")
        val recipes = apiService.searchRecipes("eggs")
        Log.d(TAG, "recipes = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenResumed completed: $it")
        }

        lifecycleScope.launch {
            Log.d(TAG, "launch for repeatOnLifecycle started")
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d(TAG, "repeatOnLifeCycle started")
                val recipes = apiService.searchRecipes("eggs")
                Log.d(TAG, "recipes = $recipes")
            }

            Log.d(TAG, "See when i am printed ...")
        }.invokeOnCompletion {
            Log.d(TAG, "launch for repeatOnLifeCycle completed: $it")
        }
    }

    private fun prepareFakeData() {
        apiService.addRecipes(mRecipes)
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