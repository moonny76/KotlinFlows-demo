package org.scarlet.android.movies.data.remote.model

import com.google.gson.annotations.SerializedName
import org.scarlet.android.movies.model.Movie

data class TMDBMovie(
    @SerializedName("id")
    val id: Int, // 580489
    @SerializedName("adult")
    val adult: Boolean?, // false
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("overview")
    val overview: String?, // After finding a host body in investigative reporter Eddie Brock, the alien symbiote must face a new enemy, Carnage, the alter ego of serial killer Cletus Kasady.
    @SerializedName("popularity")
    val popularity: Double?, // 5806.65
    @SerializedName("poster_path")
    val posterPath: String?, // /rjkmN1dniUHVYAtwuV3Tji7FsDO.jpg
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?, // 2021-09-30
    @SerializedName("title")
    val title: String?, // Venom: Let There Be Carnage
    @SerializedName("vote_average")
    val voteAverage: Double?, // 6.8
)

fun TMDBMovie.toModel(): Movie = Movie(
    id = this.id,
    overview = this.overview,
    posterPath = this.posterPath,
    backdropPath = this.backdropPath,
    title = this.title,
    releaseDate = this.releaseDate,
    voteAverage = this.voteAverage,
)

