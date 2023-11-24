package org.scarlet.android.movies.features

import androidx.lifecycle.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.scarlet.android.movies.data.MovieRepository
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource
import java.lang.IllegalArgumentException

class MovieListViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val moviesLiveData = liveData {
        emit(Resource.Loading)
        emitSource(movieRepository.getMovies().asLiveData())
    }

    val moviesFlow: StateFlow<Resource<List<Movie>>> = movieRepository.getMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    val moviesFlowV2: StateFlow<Resource<List<Movie>>> = movieRepository.getMoviesV2()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

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