package org.scarlet.android.movies.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int, // 580489
    @ColumnInfo(name = "overview")
    val overview: String?, // After finding a host body in investigative reporter Eddie Brock, the alien symbiote must face a new enemy, Carnage, the alter ego of serial killer Cletus Kasady.
    @SerializedName("poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?, // 2021-09-30
    @ColumnInfo(name = "title")
    val title: String?, // Venom: Let There Be Carnage
    @SerializedName("vote_average")
    val voteAverage: Double?, // 6.8
)
