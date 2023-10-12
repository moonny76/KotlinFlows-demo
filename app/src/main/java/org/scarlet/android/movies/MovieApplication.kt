package org.scarlet.android.movies

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MovieApplication : Application() {
    lateinit var injection: Injection

    override fun onCreate() {
        super.onCreate()
        injection = Injection(this)
    }
}