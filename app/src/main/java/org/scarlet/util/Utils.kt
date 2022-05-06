package org.scarlet.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun delim(char: String = "-", length: Int = 50) {
    println(char.repeat(length))
}

fun spaces(level: Int) = "\t".repeat(level)

fun AppCompatActivity.hideKeyboard() {
    this.currentFocus?. let {
        val imm = this
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

val log: Logger = LoggerFactory.getLogger("Coroutines")

fun log(msg: Any?) {
    log.info(msg?.toString())
}

fun CoroutineScope.log(level: Int, msg: Any?) {
    log("${spaces(level)}${msg.toString()}")
}

fun CoroutineScope.coroutineInfo(indent: Int) {
    delim()
    println("\t".repeat(indent) + "thread = ${Thread.currentThread().name}")
    println("\t".repeat(indent) + "job = ${coroutineContext[Job]}")
    println("\t".repeat(indent) + "dispatcher = ${coroutineContext[ContinuationInterceptor]}")
    println("\t".repeat(indent) + "name = ${coroutineContext[CoroutineName]}")
    println("\t".repeat(indent) + "handler = ${coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

suspend fun CoroutineScope.coroutineDynInfo(indent: Int) {
    delim()
    println("\t".repeat(indent) + "thread = ${Thread.currentThread().name}")
    println("\t".repeat(indent) + "job = ${currentCoroutineContext()[Job]}")
    println("\t".repeat(indent) + "dispatcher = ${currentCoroutineContext()[ContinuationInterceptor]}")
    delim()
}

fun scopeInfo(scope: CoroutineScope, indent: Int) {
    delim()
    println("\t".repeat(indent) + "Scope's job = ${scope.coroutineContext[Job]}")
    println("\t".repeat(indent) + "Scope's dispatcher = ${scope.coroutineContext[ContinuationInterceptor]}")
    println("\t".repeat(indent) + "Scope's name = ${scope.coroutineContext[CoroutineName]}")
    println("\t".repeat(indent) + "Scope's handler = ${scope.coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

fun Job.completeStatus(name: String = "Job", level: Int = 0) = apply {
    log("${spaces(level)}$name: isCancelled = $isCancelled")
}

fun CoroutineScope.completeStatus(name: String = "scope", level: Int = 0) = apply {
    log("${spaces(level)}$name: isCancelled = ${coroutineContext.job.isCancelled}")
}

fun CoroutineScope.onCompletion(name: String = "scope", level: Int = 0) = apply {
    coroutineContext.job.onCompletion(name, level)
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

