package org.scarlet.android.movies.data.remote.api

import org.scarlet.android.movies.data.remote.TMDBMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("3/movie/popular")
    suspend fun fetchPopularMovies(
        @Query("api_key") key: String
    ): TMDBMoviesResponse

    // https://api.themoviedb.org/3/search/movie?query=batman&api_key=dc82a4cdec67a6e11604c3484832998b
    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") key: String
    ): TMDBMoviesResponse
}