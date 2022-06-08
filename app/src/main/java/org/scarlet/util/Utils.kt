package org.scarlet.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("Coroutines")

fun log(msg: Any?) {
    log.info(msg.toString())
}

fun delim(char: String = "-", length: Int = 50) {
    log(char.repeat(length))
}

fun spaces(level: Int) = "\t".repeat(level)

fun CoroutineScope.coroutineInfo(indent: Int) {
    delim()
    log("\t".repeat(indent) + "job = ${coroutineContext[Job]}")
    log("\t".repeat(indent) + "dispatcher = ${coroutineContext[ContinuationInterceptor]}")
    log("\t".repeat(indent) + "name = ${coroutineContext[CoroutineName]}")
    log("\t".repeat(indent) + "handler = ${coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

fun Job.completeStatus(name: String = "Job", level: Int = 0) = apply {
    log("${spaces(level)}$name: isCancelled = $isCancelled")
}

fun CoroutineScope.completeStatus(name: String = "scope", level: Int = 0) = apply {
    log("${spaces(level)}$name: isCancelled = ${coroutineContext[Job]?.isCancelled}")
}

fun Job.onCompletion(name: String, level: Int = 0): Job = apply {
    invokeOnCompletion {
        log("${spaces(level)}$name: isCancelled = $isCancelled, exception = ${it?.javaClass?.name}")
    }
}

fun <T> Deferred<T>.onCompletion(name: String, level: Int = 0): Deferred<T> = apply {
    invokeOnCompletion {
        log("${spaces(level)}$name: isCancelled = $isCancelled, exception = ${it?.javaClass?.name}")
    }
}

fun AppCompatActivity.hideKeyboard() {
    this.currentFocus?. let {
        val imm = this
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
