package org.scarlet.android.movies.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.scarlet.android.movies.model.Movie

@DelicateCoroutinesApi
class FakeLocalDataSource(val scope: CoroutineScope) : LocalDataSource {

    private val mMovies = HashMap<Int, Movie>()

    private val _flow = MutableStateFlow<List<Movie>>(emptyList())
    val flow: Flow<List<Movie>> = _flow

    private val _searchFlow = MutableStateFlow(emptyList<Movie>())
    val searchFlow: Flow<List<Movie>> = _searchFlow

    private var query: String? = null

    override suspend fun insertAll(movies: List<Movie>) {
        movies.forEach { movie ->
            mMovies[movie.id] = movie
        }
        _flow.value = mMovies.values.sortedBy { it.title }

        query?.let { query ->
            _searchFlow.value = searchMovies(query)
        }
    }

    override fun clear() {
        mMovies.clear()
        _flow.value = emptyList()
        _searchFlow.value = emptyList()
    }

    override suspend fun getAllMovies(): List<Movie> {
        return mMovies.values.sortedBy { it.title!! }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        this.query = query
        return mMovies.values
            .filter { it.title!!.contains(query, true) }
            .sortedBy { it.title }
    }

    override fun getAllMoviesFlow(): Flow<List<Movie>> {
        scope.launch {
            _flow.value = getAllMovies()
        }
        return flow
    }

    override fun searchMoviesFlow(query: String): Flow<List<Movie>> {
        scope.launch {
            _searchFlow.value = searchMovies(query)
        }
        return _searchFlow
    }
}