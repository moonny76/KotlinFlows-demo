package org.scarlet.android.movies

import org.scarlet.android.movies.model.Movie

object TestData {
    val movie1 = Movie(
        id = 768449,
        backdropPath = "/6xCOWFIb1Za7jeP6rqw7SfPgkNX.jpg",
        title = "movie1",
        overview = "A seemingly cold-blooded hitman is assigned to befriend a call girl, but all hell breaks loose when he is assigned to kill her.",
        posterPath = "/8mO2ZTTOnLnaEQd1sNZAE2XBoOg.jpg",
        releaseDate = "2021-03-05",
        voteAverage = 6.5
    )

    val movie2 = Movie(
        id = 672582,
        backdropPath = "/8VmF0Cg7CZTfMeTtsF7k2Wrj38Z.jpg",
        title = "movie2",
        overview = "After finding a host body in investigative reporter Eddie Brock, the alien symbiote must face a new enemy, Carnage, the alter ego of serial killer Cletus Kasady.",
        posterPath = "/52E0LGcMKHOO91P4j6hdHKVwITP.jpg",
        releaseDate = "2021-06-30",
        voteAverage = 5.5
    )

    val mMovies = listOf(movie1, movie2)

    val movie3 = Movie(
        id = 639721,
        backdropPath = "/wfrfxivLOBtGMC98tIr2LSOeKSe.jpg",
        title = "movie3",
        overview = "The Addams get tangled up in more wacky adventures and find themselves involved in hilarious run-ins with all sorts of unsuspecting characters.",
        posterPath = "/ld7YB9vBRp1GM1DT3KmFWSmtBPB.jpg",
        releaseDate = "2021-10-01",
        voteAverage = 7.4
    )

    val movie4 = Movie(
        id = 580489,
        backdropPath = "/lNyLSOKMMeUPr1RsL4KcRuIXwHt.jpg",
        title = "movie4",
        overview = "After finding a host body in investigative reporter Eddie Brock, the alien symbiote must face a new enemy, Carnage, the alter ego of serial killer Cletus Kasady.",
        posterPath = "/rjkmN1dniUHVYAtwuV3Tji7FsDO.jpg",
        releaseDate = "2021-09-30",
        voteAverage = 6.8
    )

    val mLocalMovies = listOf(movie1, movie2)
    val mRemoteSearchedMovies = listOf(movie2, movie3)
    val mPopularMovies = listOf(movie3, movie4)
    val mAllMovies = listOf(movie1, movie2, movie3, movie4)
}