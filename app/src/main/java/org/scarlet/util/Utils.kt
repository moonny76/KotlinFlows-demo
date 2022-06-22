package org.scarlet.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
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

@ExperimentalCoroutinesApi
fun <T> Channel<T>.onClose(message: String = "Channel") = apply {
    invokeOnClose {
        log("$message: closed, exception = ${it?.javaClass?.name}")
    }
}

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun <T> BroadcastChannel<T>.onClose(message: String = "BroadCastChannel") = apply {
    invokeOnClose {
        log("$message: closed, exception = ${it?.javaClass?.name}")
    }
}

fun AppCompatActivity.hideKeyboard() {
    this.currentFocus?. let {
        val imm = this
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
