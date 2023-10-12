package org.scarlet.android.movies.data.remote

import com.google.gson.annotations.SerializedName
import org.scarlet.android.movies.data.remote.model.TMDBMovie

data class TMDBMoviesResponse(
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val movies: List<TMDBMovie>,
    @SerializedName("total_pages")
    val totalPages: Int?, // 500
    @SerializedName("total_results")
    val totalResults: Int? // 10000
)