package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminAnnouncementEntity
import com.example.data.model.ContinueWatchingEntity
import com.example.data.model.MediaItem
import com.example.data.model.toMediaItem
import com.example.ui.components.AnnouncementBanner
import com.example.ui.components.HeroBanner
import com.example.ui.components.MovieCard
import com.example.ui.theme.CineDarkBackground
import com.example.ui.theme.CineDarkSurfaceElevated
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineRedPrimary

@Composable
fun HomeScreen(
    trending: List<MediaItem>,
    popularMovies: List<MediaItem>,
    topRatedTv: List<MediaItem>,
    customMedia: List<MediaItem>,
    continueWatching: List<ContinueWatchingEntity>,
    announcements: List<AdminAnnouncementEntity>,
    isLoading: Boolean,
    isItemInWatchlist: (String) -> Boolean,
    onMediaClick: (MediaItem) -> Unit,
    onWatchClick: (MediaItem) -> Unit,
    onWatchlistToggle: (MediaItem) -> Unit,
    onTrailerClick: (MediaItem) -> Unit,
    onSeeAllClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading && trending.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CineDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = CineRedPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading Cinetra Catalog...", color = Color.White, fontSize = 13.sp)
            }
        }
        return
    }

    val heroItem = trending.firstOrNull() ?: popularMovies.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineDarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Banner
        if (heroItem != null) {
            HeroBanner(
                media = heroItem,
                isInWatchlist = isItemInWatchlist(heroItem.id),
                onWatchClick = { onWatchClick(heroItem) },
                onWatchlistClick = { onWatchlistToggle(heroItem) },
                onTrailerClick = { onTrailerClick(heroItem) },
                onCardClick = { onMediaClick(heroItem) }
            )
        }

        // Active Admin Announcement Notices
        AnnouncementBanner(announcements = announcements)

        // Continue Watching Row (if any)
        if (continueWatching.isNotEmpty()) {
            MediaSectionRow(
                title = "Continue Watching",
                icon = Icons.Default.Tv,
                iconColor = CineGold,
                onSeeAllClick = { onSeeAllClick("Continue Watching") }
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(continueWatching, key = { it.id }) { item ->
                        val dummyMedia = MediaItem(
                            id = item.id,
                            title = item.title,
                            overview = "",
                            posterPath = item.posterPath,
                            backdropPath = item.backdropPath,
                            mediaType = item.mediaType,
                            vidlyId = item.vidlyId
                        )
                        MovieCard(
                            media = dummyMedia,
                            onClick = { onWatchClick(dummyMedia) },
                            progressPercent = item.progressPercent,
                            episodeLabel = if (item.mediaType == "tv") "S${item.season}:E${item.episode}" else null
                        )
                    }
                }
            }
        }

        // Custom Cinetra / Admin Added Titles
        if (customMedia.isNotEmpty()) {
            MediaSectionRow(
                title = "Cinetra Specials & Admin Picks",
                icon = Icons.Default.Star,
                iconColor = CineGold,
                onSeeAllClick = { onSeeAllClick("Specials") }
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(customMedia, key = { it.id }) { item ->
                        MovieCard(
                            media = item,
                            onClick = { onMediaClick(item) }
                        )
                    }
                }
            }
        }

        // Trending Now Row
        if (trending.isNotEmpty()) {
            MediaSectionRow(
                title = "Trending Now",
                icon = Icons.Default.LocalFireDepartment,
                iconColor = CineRedPrimary,
                onSeeAllClick = { onSeeAllClick("Trending") }
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trending, key = { it.id }) { item ->
                        MovieCard(
                            media = item,
                            onClick = { onMediaClick(item) }
                        )
                    }
                }
            }
        }

        // Popular Movies Row
        if (popularMovies.isNotEmpty()) {
            MediaSectionRow(
                title = "Popular Movies",
                icon = Icons.Default.Movie,
                iconColor = Color(0xFF60A5FA),
                onSeeAllClick = { onSeeAllClick("Movies") }
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(popularMovies, key = { it.id }) { item ->
                        MovieCard(
                            media = item,
                            onClick = { onMediaClick(item) }
                        )
                    }
                }
            }
        }

        // Top-Rated TV Series Row
        if (topRatedTv.isNotEmpty()) {
            MediaSectionRow(
                title = "Top-Rated TV Series",
                icon = Icons.Default.Tv,
                iconColor = Color(0xFFA78BFA),
                onSeeAllClick = { onSeeAllClick("TV") }
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(topRatedTv, key = { it.id }) { item ->
                        MovieCard(
                            media = item,
                            onClick = { onMediaClick(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun MediaSectionRow(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onSeeAllClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color.White
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(4.dp)
            ) {
                Text(
                    text = "See All",
                    color = CineGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CineGold,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}
