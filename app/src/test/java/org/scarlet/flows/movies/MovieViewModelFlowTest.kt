//package org.scarlet.flows.movies
//
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import org.scarlet.demos.movies.data.LocalDataSource
//import org.scarlet.demos.movies.data.RemoteDataSource
//import org.scarlet.demos.movies.data.local.LocalDataSourceImpl
//import org.scarlet.demos.movies.data.local.MovieDatabase
//import org.scarlet.demos.movies.data.remote.RemoteMovieClient
//import org.scarlet.demos.movies.data.repository.MovieDefaultRepository
//import org.scarlet.demos.movies.features.MovieListViewModel
//import kotlinx.coroutines.*
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@ExperimentalCoroutinesApi
//@ObsoleteCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//class MovieViewModelFlowTest {
//
//    private lateinit var externalScope: CoroutineScope
//    private lateinit var database: MovieDatabase
//    private lateinit var localDataSource: LocalDataSource
//    private lateinit var remoteDataSource: RemoteDataSource
//    private lateinit var repository: MovieDefaultRepository
//
//    private lateinit var viewModel: MovieListViewModel
//
//    @Before
//    fun init() {
//        externalScope = CoroutineScope(Dispatchers.Unconfined)
//        database = buildRoomDB()
//        localDataSource = LocalDataSourceImpl(database)
//        remoteDataSource = RemoteMovieClient()
//        repository = MovieDefaultRepository(remoteDataSource, localDataSource, externalScope)
//        viewModel = MovieListViewModel(repository)
//    }
//    @Test
//    fun viewmodel_test() = runBlocking{
//        viewModel.searchMovies("batman")
//
//        val job = launch {
//            viewModel.localFirstFlowLive.collect {
//                println(it)
//            }
//        }
//
//        delay(5_000)
//
//        job.cancelAndJoin()
//    }
//
//    @Test
//    fun viewmodel_test2() = runBlocking{
//
//        viewModel.searchMovies("batman")
//
//        val job = launch {
//            viewModel.remoteFirstFlowLive.collect {
//                println("collect = $it")
//            }
//        }
//
//        delay(5_000)
//
//        job.cancelAndJoin()
//    }
//
//    private fun buildRoomDB(): MovieDatabase =
//        Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            MovieDatabase::class.java
//        )
//            .allowMainThreadQueries()
//            .build()
//
//}