package org.scarlet.android.movies.data.local.dao

import androidx.room.*
import org.scarlet.android.movies.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    @Query("DELETE FROM movie")
    fun clear()

    @Query("SELECT * FROM movie ORDER BY title")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movie WHERE title LIKE :query ORDER BY title")
    suspend fun searchMovies(query: String): List<Movie>

    /**/

    @Query("SELECT * FROM movie ORDER BY title")
    fun getAllMoviesFlow(): Flow<List<Movie>>

    @Query("SELECT * FROM movie WHERE title LIKE :query ORDER BY title")
    fun searchMoviesFlow(query: String): Flow<List<Movie>>
}