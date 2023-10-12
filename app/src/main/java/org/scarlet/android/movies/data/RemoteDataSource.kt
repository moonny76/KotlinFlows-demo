package org.scarlet.android.movies.data

import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource

interface RemoteDataSource {
    suspend fun getPopularMovies(): Resource<List<Movie>>
    suspend fun searchMovies(query: String): Resource<List<Movie>>
}