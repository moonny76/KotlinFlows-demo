package org.scarlet.android.movies.data

import kotlinx.coroutines.delay
import org.scarlet.android.movies.TestData
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource

class FakeRemoteDataSource : RemoteDataSource {
    private var mode: ResponseType = ResponseType.Success

    fun setMode(mode: ResponseType) {
        this.mode = mode
    }

    override suspend fun getPopularMovies(): Resource<List<Movie>> {
        delay(NETWORK_DELAY)
        return when (mode) {
            ResponseType.Success -> Resource.Success(TestData.mPopularMovies)
            ResponseType.Failure -> Resource.Error("Network Error")
        }
    }

    override suspend fun searchMovies(query: String): Resource<List<Movie>> {
        delay(NETWORK_DELAY)
        return when (mode) {
            ResponseType.Success -> Resource.Success(TestData.mRemoteSearchedMovies)
            ResponseType.Failure -> Resource.Error("Network Error")
        }
    }

    companion object {
        private const val NETWORK_DELAY = 1000L

        enum class ResponseType {
            Success,
            Failure
        }
    }
}

