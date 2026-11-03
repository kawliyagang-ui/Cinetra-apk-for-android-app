package com.example

import com.example.data.model.MediaItem
import com.example.data.model.PlayerServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testMovieStreamUrlGeneration() {
        val movie = MediaItem(
            id = "693134",
            title = "Dune: Part Two",
            overview = "Desert epic",
            posterPath = "/poster.jpg",
            mediaType = "movie",
            vidlyId = "693134"
        )

        // Vidly Pro movie stream url
        val vidlyUrl = movie.getStreamUrl(PlayerServer.VIDLY)
        assertEquals("https://vidly.pro/movie/693134", vidlyUrl)

        // VidSrc backup player url
        val vidSrcUrl = movie.getStreamUrl(PlayerServer.VIDSRC)
        assertEquals("https://vidsrc.to/embed/movie/693134", vidSrcUrl)

        // AutoEmbed backup player url
        val autoEmbedUrl = movie.getStreamUrl(PlayerServer.AUTOEMBED)
        assertEquals("https://player.autoembed.cc/embed/movie/693134", autoEmbedUrl)

        // MultiEmbed server url
        val multiEmbedUrl = movie.getStreamUrl(PlayerServer.MULTIEMBED)
        assertEquals("https://multiembed.mov/?video_id=693134&tmdb=1", multiEmbedUrl)
    }

    @Test
    fun testTvSeriesStreamUrlGeneration() {
        val series = MediaItem(
            id = "1399",
            title = "Game of Thrones",
            overview = "Fantasy epic",
            posterPath = "/got.jpg",
            mediaType = "tv",
            vidlyId = "1399",
            numberOfSeasons = 8,
            episodesPerSeason = 10
        )

        val vidlyTvUrl = series.getStreamUrl(PlayerServer.VIDLY, season = 3, episode = 9)
        assertEquals("https://vidly.pro/tv/1399/3/9", vidlyTvUrl)

        val vidSrcTvUrl = series.getStreamUrl(PlayerServer.VIDSRC, season = 1, episode = 1)
        assertEquals("https://vidsrc.to/embed/tv/1399/1/1", vidSrcTvUrl)

        val autoEmbedTvUrl = series.getStreamUrl(PlayerServer.AUTOEMBED, season = 2, episode = 5)
        assertEquals("https://player.autoembed.cc/embed/tv/1399/2/5", autoEmbedTvUrl)
    }

    @Test
    fun testPosterAndBackdropUrlFallback() {
        val itemWithEmptyImages = MediaItem(
            id = "test",
            title = "Test Movie",
            overview = "Test",
            posterPath = "",
            backdropPath = ""
        )
        assertTrue(itemWithEmptyImages.fullPosterUrl.startsWith("http"))
        assertTrue(itemWithEmptyImages.fullBackdropUrl.startsWith("http"))
    }

    @Test
    fun testTmdbSearchHistoryEntityCreation() {
        val entity = com.example.data.model.TmdbSearchHistoryEntity(
            query = "Gladiator II",
            timestamp = 1700000000000L
        )
        assertEquals("Gladiator II", entity.query)
        assertEquals(1700000000000L, entity.timestamp)
    }

    @Test
    fun testTmdbSearchHistoryLastFivePreservation() {
        val initialSearches = listOf(
            com.example.data.model.TmdbSearchHistoryEntity("Inception", 1000L),
            com.example.data.model.TmdbSearchHistoryEntity("Interstellar", 2000L),
            com.example.data.model.TmdbSearchHistoryEntity("Dune: Part Two", 3000L),
            com.example.data.model.TmdbSearchHistoryEntity("Oppenheimer", 4000L),
            com.example.data.model.TmdbSearchHistoryEntity("Arcane", 5000L)
        )

        // Adding 6th search query
        val newSearch = com.example.data.model.TmdbSearchHistoryEntity("Gladiator II", 6000L)
        val combined = (initialSearches + newSearch)
            .sortedByDescending { it.timestamp }
            .take(5)

        assertEquals(5, combined.size)
        assertEquals("Gladiator II", combined.first().query)
        assertEquals(listOf("Gladiator II", "Arcane", "Oppenheimer", "Dune: Part Two", "Interstellar"), combined.map { it.query })
    }
}

