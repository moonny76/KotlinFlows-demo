package org.scarlet.android.movies.data

import kotlinx.coroutines.flow.Flow
import org.scarlet.android.movies.model.Movie

interface LocalDataSource {
    suspend fun insertAll(movies: List<Movie>)

    fun clear()

    suspend fun getAllMovies(): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>

    fun getAllMoviesFlow(): Flow<List<Movie>>
    fun searchMoviesFlow(query: String): Flow<List<Movie>>

}