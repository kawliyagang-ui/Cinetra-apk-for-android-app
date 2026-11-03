package com.example.data.remote

import com.example.data.model.MediaItem

object CuratedCatalog {
    val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Sci-Fi",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western",
        10759 to "Action & Adventure",
        10765 to "Sci-Fi & Fantasy"
    )

    fun getGenreName(id: Int): String = genreMap[id] ?: "Cinema"

    val trendingTitles = listOf(
        MediaItem(
            id = "693134",
            tmdbId = 693134,
            title = "Dune: Part Two",
            overview = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a warpath of revenge against the conspirators who destroyed his family.",
            posterPath = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
            backdropPath = "/xOMo8BRK7PfcJv9JCnx7s5200SV.jpg",
            mediaType = "movie",
            releaseDate = "2024-03-01",
            voteAverage = 8.5,
            genres = listOf("Sci-Fi", "Adventure", "Drama"),
            vidlyId = "693134",
            trailerYoutubeKey = "Way9Dexny3w"
        ),
        MediaItem(
            id = "872585",
            tmdbId = 872585,
            title = "Oppenheimer",
            overview = "The story of J. Robert Oppenheimer's role in the development of the atomic bomb during World War II.",
            posterPath = "/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            backdropPath = "/rLb2cwF3Pazuxaj0sRXQ037tGI1.jpg",
            mediaType = "movie",
            releaseDate = "2023-07-21",
            voteAverage = 8.4,
            genres = listOf("Drama", "History"),
            vidlyId = "872585",
            trailerYoutubeKey = "uYPbbksJxIg"
        ),
        MediaItem(
            id = "533535",
            tmdbId = 533535,
            title = "Deadpool & Wolverine",
            overview = "A listless Wade Wilson toils in civilian life with his days as the morally flexible mercenary, Deadpool, behind him. But when his homeworld faces an existential threat, Wade must reluctantly suit-up again with an even more reluctant Wolverine.",
            posterPath = "/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            backdropPath = "/yDHYTjA3R0ne82jN4722BkODAc7.jpg",
            mediaType = "movie",
            releaseDate = "2024-07-26",
            voteAverage = 8.0,
            genres = listOf("Action", "Comedy", "Sci-Fi"),
            vidlyId = "533535",
            trailerYoutubeKey = "73_1biulkYk"
        ),
        MediaItem(
            id = "558449",
            tmdbId = 558449,
            title = "Gladiator II",
            overview = "Years after witnessing the death of the revered hero Maximus at the hands of his uncle, Lucius must enter the Colosseum after his home is conquered by the tyrannical Emperors who now lead Rome with an iron fist.",
            posterPath = "/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
            backdropPath = "/euYIwmqkmz95mnXvufEmbL69mZ9.jpg",
            mediaType = "movie",
            releaseDate = "2024-11-22",
            voteAverage = 8.1,
            genres = listOf("Action", "Adventure", "Drama"),
            vidlyId = "558449",
            trailerYoutubeKey = "4rgYUipGJNo"
        ),
        MediaItem(
            id = "945961",
            tmdbId = 945961,
            title = "Alien: Romulus",
            overview = "While scavenging the deep ends of a derelict space station, a group of young space colonizers come face to face with the most terrifying life form in the universe.",
            posterPath = "/b33nnKl1GSFbao8l3fZkyRdfkdF.jpg",
            backdropPath = "/9SSEUrSqhljBMzRe4aBTh17rUaC.jpg",
            mediaType = "movie",
            releaseDate = "2024-08-16",
            voteAverage = 7.7,
            genres = listOf("Horror", "Sci-Fi"),
            vidlyId = "945961",
            trailerYoutubeKey = "x0XDEhP4MQs"
        )
    )

    val popularMovies = listOf(
        MediaItem(
            id = "157336",
            tmdbId = 157336,
            title = "Interstellar",
            overview = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            posterPath = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            backdropPath = "/xJHokMbljvjADYdit5fK5VQsXEG.jpg",
            mediaType = "movie",
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            genres = listOf("Adventure", "Drama", "Sci-Fi"),
            vidlyId = "157336",
            trailerYoutubeKey = "zSWdZVtXT7E"
        ),
        MediaItem(
            id = "27205",
            tmdbId = 27205,
            title = "Inception",
            overview = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"inception\".",
            posterPath = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
            backdropPath = "/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg",
            mediaType = "movie",
            releaseDate = "2010-07-16",
            voteAverage = 8.4,
            genres = listOf("Action", "Sci-Fi", "Adventure"),
            vidlyId = "27205",
            trailerYoutubeKey = "YoHD9XEInc0"
        ),
        MediaItem(
            id = "155",
            tmdbId = 155,
            title = "The Dark Knight",
            overview = "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets.",
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropPath = "/nMKdUUepR0i5zn0y1T4CsSB5chy.jpg",
            mediaType = "movie",
            releaseDate = "2008-07-18",
            voteAverage = 8.8,
            genres = listOf("Drama", "Action", "Crime"),
            vidlyId = "155",
            trailerYoutubeKey = "EXeTwQWrcwY"
        ),
        MediaItem(
            id = "299534",
            tmdbId = 299534,
            title = "Avengers: Endgame",
            overview = "After the devastating events of Avengers: Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos' actions and restore balance to the universe.",
            posterPath = "/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
            backdropPath = "/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg",
            mediaType = "movie",
            releaseDate = "2019-04-26",
            voteAverage = 8.3,
            genres = listOf("Adventure", "Sci-Fi", "Action"),
            vidlyId = "299534",
            trailerYoutubeKey = "TcMBFSGVi1c"
        ),
        MediaItem(
            id = "1022789",
            tmdbId = 1022789,
            title = "Inside Out 2",
            overview = "Teenager Riley's mind headquarters is undergoing a sudden demolition to make room for something entirely unexpected: new Emotions! Joy, Sadness, Anger, Fear and Disgust aren't sure how to feel when Anxiety shows up.",
            posterPath = "/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg",
            backdropPath = "/stKGOm8wZ1IgOuLVeiobtDYqDu.jpg",
            mediaType = "movie",
            releaseDate = "2024-06-14",
            voteAverage = 7.9,
            genres = listOf("Animation", "Family", "Comedy"),
            vidlyId = "1022789",
            trailerYoutubeKey = "LEjhY15eCx0"
        )
    )

    val topRatedSeries = listOf(
        MediaItem(
            id = "1399",
            tmdbId = 1399,
            title = "Game of Thrones",
            overview = "Seven noble families fight for control of the mythical land of Westeros. Friction between the houses leads to full-scale war. All while a very ancient evil awakens in the farthest north.",
            posterPath = "/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg",
            backdropPath = "/2OMB0ynKlyIenMJWI2Dy9IWT4c.jpg",
            mediaType = "tv",
            releaseDate = "2011-04-17",
            voteAverage = 8.5,
            genres = listOf("Sci-Fi & Fantasy", "Drama", "Action & Adventure"),
            vidlyId = "1399",
            trailerYoutubeKey = "KPLWWIOCOOQ",
            numberOfSeasons = 8,
            episodesPerSeason = 10
        ),
        MediaItem(
            id = "66732",
            tmdbId = 66732,
            title = "Stranger Things",
            overview = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
            posterPath = "/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
            backdropPath = "/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
            mediaType = "tv",
            releaseDate = "2016-07-15",
            voteAverage = 8.6,
            genres = listOf("Sci-Fi & Fantasy", "Drama", "Mystery"),
            vidlyId = "66732",
            trailerYoutubeKey = "b9EkMc79ZSU",
            numberOfSeasons = 4,
            episodesPerSeason = 9
        ),
        MediaItem(
            id = "100088",
            tmdbId = 100088,
            title = "The Last of Us",
            overview = "Twenty years after modern civilization has been destroyed, Joel, a hardened survivor, is hired to smuggle Ellie, a 14-year-old girl, out of an oppressive quarantine zone.",
            posterPath = "/uKwX934q5GqV5n7qg4c4X67sZ8L.jpg",
            backdropPath = "/uDgy6hyPd82kOHh6I95FLtLnj6p.jpg",
            mediaType = "tv",
            releaseDate = "2023-01-15",
            voteAverage = 8.7,
            genres = listOf("Drama", "Sci-Fi & Fantasy", "Action & Adventure"),
            vidlyId = "100088",
            trailerYoutubeKey = "uLtkt8BonwM",
            numberOfSeasons = 1,
            episodesPerSeason = 9
        ),
        MediaItem(
            id = "94605",
            tmdbId = 94605,
            title = "Arcane",
            overview = "Amid the stark discord of twin cities Piltover and Zaun, two sisters fight on rival sides of a war between magic technologies and incompatible convictions.",
            posterPath = "/fqldf2t8ztc9aiwn397rWW2yaQi.jpg",
            backdropPath = "/rkB4LyZHo1NHXSTXYZaCRvXOXz9.jpg",
            mediaType = "tv",
            releaseDate = "2021-11-06",
            voteAverage = 8.9,
            genres = listOf("Animation", "Sci-Fi & Fantasy", "Action & Adventure"),
            vidlyId = "94605",
            trailerYoutubeKey = "fXmAurh012s",
            numberOfSeasons = 2,
            episodesPerSeason = 9
        ),
        MediaItem(
            id = "106379",
            tmdbId = 106379,
            title = "Fallout",
            overview = "The story of haves and have-nots in a world in which there's almost nothing left to have. 200 years after the apocalypse, the gentle denizens of luxury fallout shelters are forced to return to the irradiated hellscape their ancestors left behind.",
            posterPath = "/ans420g3P9Yy5W8aR0xXqG5hG5h.jpg",
            backdropPath = "/mo74F0g2mS0hM9eHl6x0kK7A3P7.jpg",
            mediaType = "tv",
            releaseDate = "2024-04-10",
            voteAverage = 8.4,
            genres = listOf("Sci-Fi & Fantasy", "Action & Adventure", "Drama"),
            vidlyId = "106379",
            trailerYoutubeKey = "V-mugKDQDlg",
            numberOfSeasons = 1,
            episodesPerSeason = 8
        )
    )

    val allCurated = (trendingTitles + popularMovies + topRatedSeries).distinctBy { it.id }
}
