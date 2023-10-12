package org.scarlet.android.movies.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.scarlet.android.movies.TestData.mLocalMovies
import org.scarlet.android.movies.TestData.mPopularMovies
import org.scarlet.android.movies.TestData.movie1
import org.scarlet.android.movies.TestData.movie2
import org.scarlet.android.movies.TestData.movie3
import org.scarlet.android.movies.data.FakeLocalDataSource
import org.scarlet.android.movies.data.FakeRemoteDataSource
import org.scarlet.android.movies.data.MovieRepository
import org.scarlet.android.movies.model.Movie
import org.scarlet.util.Resource
import org.scarlet.util.log

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
class MovieDefaultRepositoryTest {

    // SUT
    lateinit var repository: MovieRepository

    val scope = TestScope()
    var localDataSource: FakeLocalDataSource = FakeLocalDataSource(scope)
    val remoteDataSource: FakeRemoteDataSource = FakeRemoteDataSource()

    @Before
    fun setup() {
        repository = MovieDefaultRepository.getInstance(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        repository.reset()
    }

    @Test
    fun `getMovies() - should return all local movies and popular remote movies when query_empty`() =
        runTest {
            // Arrange (Given)
            localDataSource.insertAll(mLocalMovies)
            val movies: Flow<Resource<List<Movie>>> = repository.getMovies()

            // Act (When)
            repository.searchMovies("")

            // Assert (Then)
            movies.test {
                assertThat(awaitItem()).isEqualTo(Resource.Loading) // Loading
                assertThat(awaitItem()).isEqualTo(Resource.Success(mLocalMovies)) // All local movies
                assertThat(awaitItem()).isEqualTo(Resource.Success(mPopularMovies)) // Popular remote movies
                log(cancelAndConsumeRemainingEvents())
            }
        }

    @Test
    fun `getMovies() - return searched local movies and remote searched movies when query not empty`() =
        runTest {
            // Arrange (Given)
            localDataSource.insertAll(listOf(movie1, movie2))
            val movies: Flow<Resource<List<Movie>>> = repository.getMovies()

            // Act (When)
            repository.searchMovies("movie")

            // Assert (Then)
            movies.test {
                assertThat(awaitItem()).isEqualTo(Resource.Loading) // Loading
                assertThat(awaitItem()).isEqualTo(
                    Resource.Success(
                        listOf(
                            movie1,
                            movie2
                        )
                    )
                ) // searched local movies
                assertThat(awaitItem()).isEqualTo(
                    Resource.Success(
                        listOf(
                            movie2,
                            movie3
                        )
                    )
                ) // searched remote movies
                log(cancelAndConsumeRemainingEvents())
            }
        }

    @Test
    fun `getMoviesV2() - return searched local movies and then merged movies when query empty`() =
        scope.runTest {
            // Arrange (Given)
            localDataSource.insertAll(listOf(movie1, movie2))
            val movies: Flow<Resource<List<Movie>>> = repository.getMoviesV2()

            // Act (When)
            repository.searchMovies("")

            // Assert (Then)
            movies.test {
                assertThat(awaitItem()).isEqualTo(Resource.Loading) // Loading
                var res = cvtToTitle((awaitItem() as Resource.Success<List<Movie>>).data!!)
                assertThat(res).isEqualTo(listOf("movie1", "movie2")) // All local movies
                res = cvtToTitle((awaitItem() as Resource.Success<List<Movie>>).data!!)
                assertThat(res.sorted()).isEqualTo(
                    listOf(
                        "movie1",
                        "movie2",
                        "movie3",
                        "movie4"
                    )
                ) // merged movies
                log(cancelAndConsumeRemainingEvents())
            }
        }


    private fun cvtToTitle(movies: List<Movie>): List<String> = movies.map { it.title!! }
}