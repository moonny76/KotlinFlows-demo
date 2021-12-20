package org.scarlet.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.ContinuationInterceptor

fun delim(char: String = "-", length: Int = 50) {
    println(char.repeat(length))
}

fun spaces(level: Int) = "\t".repeat(level)

fun CoroutineScope.coroutineInfo(indent: Int) {
    delim()
    println("\t".repeat(indent) + "thread = ${Thread.currentThread().name}")
    println("\t".repeat(indent) + "job = ${coroutineContext[Job]}")
    println("\t".repeat(indent) + "dispatcher = ${coroutineContext[ContinuationInterceptor]}")
    println("\t".repeat(indent) + "name = ${coroutineContext[CoroutineName]}")
    println("\t".repeat(indent) + "handler = ${coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

fun AppCompatActivity.hideKeyboard() {
    this.currentFocus?. let {
        val imm = this
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
