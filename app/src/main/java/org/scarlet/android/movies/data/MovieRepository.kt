package org.scarlet.android.movies.data

import org.scarlet.util.Resource
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.VisibleForTesting
import org.scarlet.android.movies.model.Movie

interface MovieRepository {
    fun searchMovies(query: String)

    fun getMovies(): Flow<Resource<List<Movie>>>
    fun getMoviesV2(): Flow<Resource<List<Movie>>>

    @VisibleForTesting
    fun reset()
}