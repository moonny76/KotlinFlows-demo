package org.scarlet.android.movies.data.remote

import android.util.Log
import org.scarlet.android.movies.TAG
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource
import org.scarlet.android.movies.data.RemoteDataSource
import org.scarlet.android.movies.data.remote.model.toModel

class RemoteMovieClient : RemoteDataSource {

    override suspend fun getPopularMovies(): Resource<List<Movie>> {
        Log.d(TAG, "[RemoteMovieClient] getPopularMovies: called")
        return try {
            val movies = ServiceGenerator.fetchPopularMovies().movies
            Resource.Success(movies.map { it.toModel() })
        } catch (ex: Throwable) {
            Resource.Error(ex.message)
        }
    }

    override suspend fun searchMovies(query: String): Resource<List<Movie>> {
        Log.d(TAG, "[RemoteMovieClient] searchMovies($query): called")
        return try {
            val movies = ServiceGenerator.searchMovies(query).movies
            Resource.Success(movies.map { it.toModel() })
        } catch (ex: Throwable) {
            Resource.Error(ex.message)
        }
    }
}
