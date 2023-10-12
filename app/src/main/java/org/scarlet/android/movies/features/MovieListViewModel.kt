package org.scarlet.android.movies.features

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import org.scarlet.android.movies.data.MovieRepository
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource
import java.lang.IllegalArgumentException

class MovieListViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val moviesFlow: Flow<Resource<List<Movie>>> = movieRepository.getMovies()
    val moviesFlowV2: Flow<Resource<List<Movie>>> = movieRepository.getMoviesV2()

    fun searchMovies(query: String) = movieRepository.searchMovies(query)
}

@Suppress("UNCHECKED_CAST")
class MovieListViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MovieListViewModel::class.java))
            throw IllegalArgumentException("No such viewmodel")
        return MovieListViewModel(repository) as T
    }
}