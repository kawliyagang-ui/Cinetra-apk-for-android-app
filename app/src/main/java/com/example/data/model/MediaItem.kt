package com.example.data.model

data class MediaItem(
    val id: String,
    val tmdbId: Long? = null,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String = "",
    val mediaType: String = "movie", // "movie" or "tv"
    val releaseDate: String = "",
    val voteAverage: Double = 0.0,
    val genres: List<String> = emptyList(),
    val vidlyId: String = "",
    val isCustom: Boolean = false,
    val trailerYoutubeKey: String? = null,
    val numberOfSeasons: Int = 1,
    val episodesPerSeason: Int = 10
) {
    val fullPosterUrl: String
        get() = when {
            posterPath.isBlank() -> "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500"
            posterPath.startsWith("http") -> posterPath
            else -> "https://image.tmdb.org/t/p/w500$posterPath"
        }

    val fullBackdropUrl: String
        get() = when {
            backdropPath.isNotBlank() && backdropPath.startsWith("http") -> backdropPath
            backdropPath.isNotBlank() -> "https://image.tmdb.org/t/p/w1280$backdropPath"
            posterPath.isNotBlank() && posterPath.startsWith("http") -> posterPath
            posterPath.isNotBlank() -> "https://image.tmdb.org/t/p/w1280$posterPath"
            else -> "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1280"
        }

    val streamId: String
        get() = if (vidlyId.isNotBlank()) vidlyId else (tmdbId?.toString() ?: id)

    fun getStreamUrl(server: PlayerServer = PlayerServer.VIDLY, season: Int = 1, episode: Int = 1): String {
        val sId = streamId
        return when (server) {
            PlayerServer.VIDLY -> {
                if (mediaType == "tv") {
                    "https://vidly.pro/tv/$sId/$season/$episode"
                } else {
                    if (sId.startsWith("http")) sId else "https://vidly.pro/movie/$sId"
                }
            }
            PlayerServer.VIDSRC -> {
                if (mediaType == "tv") {
                    "https://vidsrc.to/embed/tv/$sId/$season/$episode"
                } else {
                    "https://vidsrc.to/embed/movie/$sId"
                }
            }
            PlayerServer.AUTOEMBED -> {
                if (mediaType == "tv") {
                    "https://player.autoembed.cc/embed/tv/$sId/$season/$episode"
                } else {
                    "https://player.autoembed.cc/embed/movie/$sId"
                }
            }
            PlayerServer.MULTIEMBED -> {
                if (mediaType == "tv") {
                    "https://multiembed.mov/?video_id=$sId&tmdb=1&s=$season&e=$episode"
                } else {
                    "https://multiembed.mov/?video_id=$sId&tmdb=1"
                }
            }
        }
    }
}

enum class PlayerServer(val displayName: String, val speed: String) {
    VIDLY("Vidly Pro (Primary)", "Ultra Fast"),
    VIDSRC("VidSrc Backup", "HD 1080p"),
    AUTOEMBED("AutoEmbed Backup", "Adaptive"),
    MULTIEMBED("MultiEmbed Server", "High Speed")
}

// Extension to map CustomMediaEntity to MediaItem
fun CustomMediaEntity.toMediaItem(): MediaItem {
    return MediaItem(
        id = id,
        tmdbId = tmdbId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        mediaType = mediaType,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genres = genres.split(",").map { it.trim() }.filter { it.isNotBlank() },
        vidlyId = vidlyId,
        isCustom = true,
        trailerYoutubeKey = trailerYoutubeKey,
        numberOfSeasons = numberOfSeasons,
        episodesPerSeason = episodesPerSeason
    )
}

// Extension to map WatchlistEntity to MediaItem
fun WatchlistEntity.toMediaItem(): MediaItem {
    return MediaItem(
        id = id,
        tmdbId = tmdbId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        mediaType = mediaType,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genres = genres.split(",").map { it.trim() }.filter { it.isNotBlank() },
        vidlyId = vidlyId,
        isCustom = false
    )
}
