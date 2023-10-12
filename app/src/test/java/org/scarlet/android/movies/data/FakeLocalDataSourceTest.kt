package org.scarlet.android.movies.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.scarlet.android.movies.TestData
import org.scarlet.android.movies.model.Movie

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
class FakeLocalDataSourceTest {

    // SUT
    lateinit var localDataSource: FakeLocalDataSource

    val scope = TestScope()

    @Before
    fun setup() {
        localDataSource = FakeLocalDataSource(scope)
    }

    @Test
    fun `should insert and get all movies`() = runTest {
        // Arrange (Given)
        localDataSource.insertAll(listOf(TestData.movie1, TestData.movie2))

        // Act (When)
        val movies = localDataSource.getAllMovies()

        // Assert (Then)
        assertThat(movies).isEqualTo(listOf(TestData.movie1, TestData.movie2))
    }

    @Test
    fun `should search movies`() = runTest {
        // Arrange (Given)
        localDataSource.insertAll(listOf(TestData.movie1, TestData.movie2))

        // Act (When)
        val movies = localDataSource.searchMovies("movie")

        // Assert (Then)
        assertThat(movies).isEqualTo(listOf(TestData.movie1, TestData.movie2))
    }

    @Test
    fun `should update all movies flow`() = scope.runTest {
        // Arrange (Given)
        localDataSource.insertAll(listOf(TestData.movie1, TestData.movie2))

        // Act (When)
        // Assert (Then)
        val flow = localDataSource.searchMoviesFlow("movie")

        flow.test {
            assertThat(awaitItem()).isEqualTo(emptyList<Movie>())
            assertThat(awaitItem().map { it.title }).isEqualTo(listOf("movie1", "movie2"))

            localDataSource.insertAll(listOf(TestData.movie3))
            assertThat(awaitItem().map { it.title }).isEqualTo(listOf("movie1", "movie2", "movie3"))
        }
    }

}