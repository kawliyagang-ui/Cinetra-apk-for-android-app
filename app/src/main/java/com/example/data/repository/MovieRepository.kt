package com.example.data.repository

import com.example.data.local.MediaDao
import com.example.data.model.AdminAnnouncementEntity
import com.example.data.model.ContinueWatchingEntity
import com.example.data.model.CustomMediaEntity
import com.example.data.model.MediaItem
import com.example.data.model.TmdbItemDto
import com.example.data.model.TmdbSearchHistoryEntity
import com.example.data.model.WatchlistEntity
import com.example.data.remote.CuratedCatalog
import com.example.data.remote.TmdbClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MovieRepository(private val mediaDao: MediaDao) {

    val watchlist: Flow<List<WatchlistEntity>> = mediaDao.getAllWatchlist()
    val continueWatching: Flow<List<ContinueWatchingEntity>> = mediaDao.getAllContinueWatching()
    val customMedia: Flow<List<CustomMediaEntity>> = mediaDao.getAllCustomMedia()
    val allAnnouncements: Flow<List<AdminAnnouncementEntity>> = mediaDao.getAllAnnouncements()
    val activeAnnouncements: Flow<List<AdminAnnouncementEntity>> = mediaDao.getActiveAnnouncements()
    val tmdbSearchHistory: Flow<List<TmdbSearchHistoryEntity>> = mediaDao.getRecentTmdbSearches()

    fun isWatchlisted(id: String): Flow<Boolean> = mediaDao.isWatchlisted(id)

    suspend fun initSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        // Seed default announcement if none exists
        val currentAnnouncements = mediaDao.getAllAnnouncements().firstOrNull()
        if (currentAnnouncements.isNullOrEmpty()) {
            mediaDao.insertAnnouncement(
                AdminAnnouncementEntity(
                    title = "Welcome to Cinetra!",
                    message = "Stream movies & TV series in HD with instant Vidly Pro playback. Switch backup servers if needed!",
                    type = "INFO",
                    active = true
                )
            )
        }

        val currentCustom = mediaDao.getAllCustomMedia().firstOrNull()
        if (currentCustom.isNullOrEmpty()) {
            mediaDao.insertCustomMedia(
                CustomMediaEntity(
                    id = "custom_cyberpunk",
                    title = "Cyberpunk: Edgerunners",
                    overview = "A street kid trying to survive in a technology and body modification-obsessed city of the future. Having everything to lose, he chooses to stay alive by becoming an edgerunner.",
                    posterPath = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500",
                    backdropPath = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1280",
                    mediaType = "tv",
                    releaseDate = "2022",
                    voteAverage = 8.6,
                    genres = "Animation, Sci-Fi, Action",
                    vidlyId = "105248",
                    numberOfSeasons = 1,
                    episodesPerSeason = 10
                )
            )
        }
    }

    suspend fun getTrending(): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val response = TmdbClient.apiService.getTrending(TmdbClient.apiKey)
            val items = response.results.filter { it.posterPath != null }.map { it.toMediaItem() }
            if (items.isNotEmpty()) items else CuratedCatalog.trendingTitles
        } catch (e: Exception) {
            CuratedCatalog.trendingTitles
        }
    }

    suspend fun getPopularMovies(): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val response = TmdbClient.apiService.getPopularMovies(TmdbClient.apiKey)
            val items = response.results.filter { it.posterPath != null }.map { it.toMediaItem(defaultType = "movie") }
            if (items.isNotEmpty()) items else CuratedCatalog.popularMovies
        } catch (e: Exception) {
            CuratedCatalog.popularMovies
        }
    }

    suspend fun getTopRatedTv(): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val response = TmdbClient.apiService.getTopRatedTv(TmdbClient.apiKey)
            val items = response.results.filter { it.posterPath != null }.map { it.toMediaItem(defaultType = "tv") }
            if (items.isNotEmpty()) items else CuratedCatalog.topRatedSeries
        } catch (e: Exception) {
            CuratedCatalog.topRatedSeries
        }
    }

    suspend fun searchTitles(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = TmdbClient.apiService.searchMulti(apiKey = TmdbClient.apiKey, query = query)
            val results = response.results
                .filter { it.posterPath != null && (it.mediaType == "movie" || it.mediaType == "tv" || it.mediaType == null) }
                .map { it.toMediaItem() }
            results
        } catch (e: Exception) {
            // Local fallback search in curated catalog
            CuratedCatalog.allCurated.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.overview.contains(query, ignoreCase = true) ||
                it.genres.any { g -> g.contains(query, ignoreCase = true) }
            }
        }
    }

    suspend fun fetchTrailerYoutubeKey(id: Long, mediaType: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = if (mediaType == "tv") {
                TmdbClient.apiService.getTvVideos(id, TmdbClient.apiKey)
            } else {
                TmdbClient.apiService.getMovieVideos(id, TmdbClient.apiKey)
            }
            val trailer = response.results.firstOrNull {
                it.site.equals("YouTube", ignoreCase = true) &&
                (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true))
            } ?: response.results.firstOrNull { it.site.equals("YouTube", ignoreCase = true) }
            trailer?.key
        } catch (e: Exception) {
            null
        }
    }

    // Watchlist
    suspend fun toggleWatchlist(item: MediaItem) = withContext(Dispatchers.IO) {
        val isSaved = mediaDao.isWatchlisted(item.id).firstOrNull() ?: false
        if (isSaved) {
            mediaDao.removeFromWatchlist(item.id)
        } else {
            mediaDao.addToWatchlist(
                WatchlistEntity(
                    id = item.id,
                    tmdbId = item.tmdbId,
                    title = item.title,
                    overview = item.overview,
                    posterPath = item.posterPath,
                    backdropPath = item.backdropPath,
                    mediaType = item.mediaType,
                    releaseDate = item.releaseDate,
                    voteAverage = item.voteAverage,
                    genres = item.genres.joinToString(", "),
                    vidlyId = item.vidlyId
                )
            )
        }
    }

    // Continue Watching
    suspend fun saveContinueWatching(
        item: MediaItem,
        season: Int = 1,
        episode: Int = 1,
        progressPercent: Int = 25
    ) = withContext(Dispatchers.IO) {
        mediaDao.upsertContinueWatching(
            ContinueWatchingEntity(
                id = item.id,
                title = item.title,
                posterPath = item.posterPath,
                backdropPath = item.backdropPath,
                mediaType = item.mediaType,
                season = season,
                episode = episode,
                progressPercent = progressPercent,
                lastWatchedAt = System.currentTimeMillis(),
                vidlyId = item.vidlyId
            )
        )
    }

    suspend fun removeContinueWatching(id: String) = withContext(Dispatchers.IO) {
        mediaDao.removeContinueWatching(id)
    }

    // Custom Media (Admin)
    suspend fun insertCustomMedia(entity: CustomMediaEntity) = withContext(Dispatchers.IO) {
        mediaDao.insertCustomMedia(entity)
    }

    suspend fun updateCustomMedia(entity: CustomMediaEntity) = withContext(Dispatchers.IO) {
        mediaDao.updateCustomMedia(entity)
    }

    suspend fun deleteCustomMedia(id: String) = withContext(Dispatchers.IO) {
        mediaDao.deleteCustomMedia(id)
    }

    // Announcements (Admin)
    suspend fun insertAnnouncement(title: String, message: String, type: String) = withContext(Dispatchers.IO) {
        mediaDao.insertAnnouncement(
            AdminAnnouncementEntity(
                title = title,
                message = message,
                type = type,
                active = true
            )
        )
    }

    suspend fun updateAnnouncement(announcement: AdminAnnouncementEntity) = withContext(Dispatchers.IO) {
        mediaDao.updateAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(id: Int) = withContext(Dispatchers.IO) {
        mediaDao.deleteAnnouncement(id)
    }

    suspend fun toggleAnnouncementActive(id: Int, active: Boolean) = withContext(Dispatchers.IO) {
        mediaDao.setAnnouncementActive(id, active)
    }

    // TMDb Search History (Room Database - Last 5 items)
    suspend fun saveTmdbSearch(query: String) = withContext(Dispatchers.IO) {
        val clean = query.trim()
        if (clean.isBlank()) return@withContext
        mediaDao.insertTmdbSearch(
            TmdbSearchHistoryEntity(
                query = clean,
                timestamp = System.currentTimeMillis()
            )
        )
        mediaDao.trimTmdbSearchHistory()
    }

    suspend fun deleteTmdbSearch(query: String) = withContext(Dispatchers.IO) {
        mediaDao.deleteTmdbSearch(query.trim())
    }

    suspend fun clearTmdbSearchHistory() = withContext(Dispatchers.IO) {
        mediaDao.clearTmdbSearchHistory()
    }
}


// Extension to map TmdbItemDto to MediaItem
fun TmdbItemDto.toMediaItem(defaultType: String = "movie"): MediaItem {
    val mType = mediaType ?: defaultType
    val mTitle = title ?: name ?: "Untitled"
    val date = releaseDate ?: firstAirDate ?: ""
    val genreList = genreIds?.map { CuratedCatalog.getGenreName(it) } ?: listOf("Cinema")

    return MediaItem(
        id = id.toString(),
        tmdbId = id,
        title = mTitle,
        overview = overview ?: "No overview available.",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        mediaType = mType,
        releaseDate = date,
        voteAverage = voteAverage ?: 0.0,
        genres = genreList,
        vidlyId = id.toString(),
        isCustom = false,
        numberOfSeasons = numberOfSeasons ?: 1,
        episodesPerSeason = numberOfEpisodes ?: 10
    )
}
