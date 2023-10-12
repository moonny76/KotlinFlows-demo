package org.scarlet.android.movies.data.repository

import android.util.Log
import org.scarlet.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import org.scarlet.android.movies.TAG
import org.scarlet.android.movies.data.LocalDataSource
import org.scarlet.android.movies.data.MovieRepository
import org.scarlet.android.movies.data.RemoteDataSource
import org.scarlet.android.movies.model.Movie

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MovieDefaultRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : MovieRepository {

    private val queryChannel = ConflatedBroadcastChannel<String>()

    override fun searchMovies(query: String) {
        Log.d(TAG, "[Repository] searchMovies: query = $query")
        queryChannel.trySend(query)
    }

    override fun getMovies(): Flow<Resource<List<Movie>>> = flow {
        Log.e(TAG, "[Repository] getMovies flow")

        queryChannel.openSubscription().consumeEach { query ->
            Log.d(TAG, "[Repository] getMovies: emit Resource.Loading")
            emit(Resource.Loading)

            val cachedMovies = when {
                query.isEmpty() -> localDataSource.getAllMovies()
                else -> localDataSource.searchMovies(query)
            }
            Log.d(
                TAG,
                "[Repository] getMovies(cached movies): emit Resource.Success(cachedMovies): $cachedMovies"
            )
            emit(Resource.Success(cachedMovies))

            val newMovies = when {
                query.isEmpty() -> remoteDataSource.getPopularMovies()
                else -> remoteDataSource.searchMovies(query)
            }
            Log.d(
                TAG,
                "[Repository] getMovies(new movies): emit Resource.Success(newMovies): $newMovies"
            )
            emit(newMovies)

            saveToDB(newMovies)
        }
    }.catch { ex ->
        emit(Resource.Error(ex.message))
    }

    private suspend fun saveToDB(resource: Resource<List<Movie>>) {
        Log.d(TAG, "[MovieDefaultRepository] saveToDB: called")
        when (resource) {
            is Resource.Success -> localDataSource.insertAll(resource.data!!)
            else -> Log.d(TAG, "[MovieDefaultRepository] saveToDB(Loading or Fail)")
        }
    }

    override fun getMoviesV2(): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading)

        // Do concurrently
        // - Fetch movies from local DB and emit it:
        //     if query is empty, call localDataSource.getAllMoviesFlow()
        //     else call localDataSource.searchMoviesFlow(query)
        // - Fetch movies from remote API and save then to local DB:
        //    if query is empty, call remoteDataSource.getPopularMovies()
        //    else call remoteDataSource.searchMovies(query)

        TODO()
    }

    private suspend fun loadFromApi() {
        queryChannel.openSubscription().consumeEach { query ->
            Log.d(TAG, "loadFromApi: processing ....")

            val newMovies = when {
                query.isEmpty() -> remoteDataSource.getPopularMovies()
                else -> remoteDataSource.searchMovies(query)
            }
            saveToDB(newMovies)
        }
    }

    override fun reset() {
        INSTANCE = null
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource) =
            INSTANCE ?: synchronized(this) {
                MovieDefaultRepository(remoteDataSource, localDataSource).also {
                    INSTANCE = it
                }
            }

        fun reset() {
            INSTANCE = null
        }
    }
}
