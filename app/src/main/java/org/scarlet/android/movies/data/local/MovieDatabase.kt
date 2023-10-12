package org.scarlet.android.movies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.scarlet.android.movies.data.local.dao.MovieDao
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Converters

@TypeConverters(Converters::class)
@Database(entities = [Movie::class], exportSchema = false, version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}