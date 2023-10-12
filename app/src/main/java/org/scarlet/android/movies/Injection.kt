package org.scarlet.android.movies

import android.content.Context
import androidx.room.Room
import org.scarlet.android.movies.data.local.LocalDataSourceImpl
import org.scarlet.android.movies.data.local.MovieDatabase
import org.scarlet.android.movies.data.remote.RemoteMovieClient
import kotlinx.coroutines.*
import org.scarlet.android.movies.data.MovieRepository
import org.scarlet.android.movies.data.repository.MovieDefaultRepository

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class Injection(private val context: Context) {

    @DelicateCoroutinesApi
    var repository: MovieRepository? = null
        get() {
            return field ?: MovieDefaultRepository.getInstance(
                RemoteMovieClient(), LocalDataSourceImpl(createDatabase())
            ).also {
                field = it
            }
        }

    private fun createDatabase(): MovieDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            MovieDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
}
