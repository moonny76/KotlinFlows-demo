package org.scarlet.flows.genesis

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

suspend fun foo(block: suspend () -> Unit) {
    println("foo currentCoroutineContext = ${currentCoroutineContext()}")
    val scope = CoroutineScope(Job())
    scope.launch {
        block()
    }.join()
}

object Foo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("runBlocking: coroutineContext = $coroutineContext")

        foo {
            println("call foo: coroutineContext = $coroutineContext")
            println("call foo: currentCoroutineContext = ${currentCoroutineContext()}")
        }
    }
}

object ObserverPattern {

    interface Observer<T> {
        fun update(value: T)
    }

    interface Subject<T> {
        fun register(observer: Observer<T>)
    }

    @JvmStatic
    fun main(args: Array<String>) {

        val observer = object : Observer<Int> {
            override fun update(value: Int) {
                println("Value = $value")
            }
        }

        val subject = object : Subject<Int> {
            lateinit var observer: Observer<Int>

            override fun register(observer: Observer<Int>) {
                this.observer = observer
            }

            fun process() {
                observer.update(1)
                observer.update(2)
                observer.update(3)
            }
        }

        subject.register(observer)

        subject.process()
    }
}

object ObserverPattern2 {

    interface Observer<T> {
        fun update(value: T)
    }

    interface Subject<T> {
        fun register(observer: Observer<T>)
        fun process(block: Observer<T>.() -> Unit)
    }

    @JvmStatic
    fun main(args: Array<String>) {

        val observer = object : Observer<Int> {
            override fun update(value: Int) {
                println("Value = $value")
            }
        }

        val subject = object : Subject<Int> {
            lateinit var observer: Observer<Int>

            override fun register(observer: Observer<Int>) {
                this.observer = observer
            }

            override fun process(block: Observer<Int>.() -> Unit) {
                observer.block()
            }
        }

        subject.register(observer)

        subject.process {
            update(1)
            update(2)
            update(3)
        }
    }
}

object ColdSubject {

    interface Observer<T> {
        suspend fun update(value: T)
    }

    interface Subject<T> {
        suspend fun register(observer: Observer<T>)
    }

    fun <T> process(block: suspend Observer<T>.() -> Unit): Subject<T> =
        object : Subject<T> {
            override suspend fun register(observer: Observer<T>) {
                observer.block()
            }
        }

    suspend fun <T> Subject<T>.trigger(action: suspend (T) -> Unit) {
        register(object : Observer<T> {
            override suspend fun update(value: T) {
                action(value)
            }
        })
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val subject: Subject<Int> = process {
            repeat(3) {
                println("Generating next value ...")
                update(it + 1)
                delay(1000)
            }
        }

        subject.trigger {
            println("Value = $it")
            delay(2000)
            println("Value = $it done")
        }
    }

}


object MyFlowTypes {

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
//        val flow = flow {
//            (1..3).forEach {
//                emit(it)
//            }
//        }
//
//        flow.map {
//            it to it * it
//        }.collect {
//            log(it)
//        }
//
//        delim()
//
//        flow.flatMapConcat {
//            flow {
//                emit(it)
//                emit(it * it)
//            }
//        }.collect {
//            log(it)
//        }
//
//        delim()
//
//        flow {
//            repeat(3) {
//                emit(it)
//                delay(100)
//            }
//        }.flatMapLatest {
//            flow {
//                emit("first: $it")
//                delay(150)
//                emit("second: $it")
//            }
//        }.collect {
//            log(it)
//        }

    }
}