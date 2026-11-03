package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: String,
    val tmdbId: Long? = null,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String = "",
    val mediaType: String = "movie",
    val releaseDate: String = "",
    val voteAverage: Double = 0.0,
    val genres: String = "", // Comma-separated
    val vidlyId: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "continue_watching")
data class ContinueWatchingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val posterPath: String,
    val backdropPath: String = "",
    val mediaType: String = "movie",
    val season: Int = 1,
    val episode: Int = 1,
    val progressPercent: Int = 0,
    val lastWatchedAt: Long = System.currentTimeMillis(),
    val vidlyId: String = ""
)

@Entity(tableName = "custom_media")
data class CustomMediaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String = "",
    val mediaType: String = "movie",
    val releaseDate: String = "",
    val voteAverage: Double = 8.0,
    val genres: String = "Action, Drama",
    val vidlyId: String,
    val tmdbId: Long? = null,
    val trailerYoutubeKey: String? = null,
    val numberOfSeasons: Int = 1,
    val episodesPerSeason: Int = 10,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AdminAnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String = "INFO", // "INFO", "WARNING", "UPDATE"
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tmdb_search_history")
data class TmdbSearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

