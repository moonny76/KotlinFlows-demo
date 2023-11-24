package org.scarlet.android.movies.data.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.scarlet.android.movies.data.remote.api.MovieApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ServiceGenerator {
    /**
     * You need to define your own API key here
     * ```
     * const val API_KEY = "your private API_KEY".
     * ```
     */
    private const val BASE_URL = "https://api.themoviedb.org/"

    //    const val API_KEY = "your private API_KEY"
    const val API_KEY = "your private API_KEY"

    suspend fun fetchPopularMovies(): TMDBMoviesResponse =
        movieApi.fetchPopularMovies(API_KEY)

    suspend fun searchMovies(query: String): TMDBMoviesResponse =
        movieApi.searchMovies(query, API_KEY)

    private var movieApi: MovieApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
            )
        )
        .build()
        .create()
}
