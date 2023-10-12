package org.scarlet.android.movies.data.local

import android.util.Log
import org.scarlet.android.movies.data.LocalDataSource
import org.scarlet.android.movies.model.Movie
import kotlinx.coroutines.flow.Flow
import org.scarlet.android.movies.TAG

class LocalDataSourceImpl(
    private val database: MovieDatabase
) : LocalDataSource {

    override suspend fun insertAll(movies: List<Movie>) {
        Log.d(TAG, "[LocalDataSourceImpl] insertAll(): called, movies = $movies")
        database.movieDao().insertAll(movies)
    }

    override fun clear() {
        Log.d(TAG, "[LocalDataSourceImpl] clear(): called")
        database.movieDao().clear()
    }

    override suspend fun getAllMovies(): List<Movie> {
        Log.d(TAG, "[LocalDataSourceImpl] getAllMovies(): called")
        return try {
            database.movieDao().getAllMovies()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        Log.d(TAG, "[LocalDataSourceImpl] searchMoviesLive($query}): called")
        return database.movieDao().searchMovies("%$query%")
    }

    /**/

    override fun searchMoviesFlow(query: String): Flow<List<Movie>> {
        Log.d(TAG, "[LocalDataSourceImpl] searchMoviesFlow(${query}): called")
        return database.movieDao().searchMoviesFlow("%$query%")
    }

    override fun getAllMoviesFlow(): Flow<List<Movie>> {
        Log.d(TAG, "[LocalDataSourceImpl]: getAllMoviesFlow(): called")
        return database.movieDao().getAllMoviesFlow()
    }

}