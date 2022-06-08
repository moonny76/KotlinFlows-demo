package org.scarlet.util

/**
 * A generic class that holds a value with its loading status.
 */
sealed class Resource<out R> {
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    object Empty : Resource<Nothing>()

    override fun toString(): String =
        when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[message=$message]"
            is Loading -> "Loading"
            is Empty -> "Empty"
        }
}

fun <T, V> Resource<T>.map(mapper: (T) -> V): Resource<V> {
    return when (this) {
        is Resource.Success<T> -> Resource.Success(this.data?.let { mapper(it) })
        else -> this as Resource<V>
//        is Resource.Error -> Resource.Error(this.message)
//        is Resource.Loading -> Resource.Loading
//        is Resource.Empty -> Resource.Empty
    }
}

