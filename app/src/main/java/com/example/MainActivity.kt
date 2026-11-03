package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.MovieRepository
import com.example.ui.components.StreamPlayerView
import com.example.ui.components.TrailerModal
import com.example.ui.screens.ContinueWatchingScreen
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CineDarkBackground
import com.example.ui.theme.CineDarkSurface
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineRedPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MovieViewModel
import com.example.ui.viewmodel.MovieViewModelFactory
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CinetraApp()
            }
        }
    }
}

@Composable
fun CinetraApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = MovieRepository(database.mediaDao())

    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(repository)
    )

    val userState by movieViewModel.uiState.collectAsState()
    val watchlist by movieViewModel.watchlist.collectAsState()
    val continueWatching by movieViewModel.continueWatching.collectAsState()
    val activeAnnouncements by movieViewModel.activeAnnouncements.collectAsState()
    val searchHistory by movieViewModel.searchHistory.collectAsState()

    val isPlayerScreen = userState.currentScreen is Screen.Player

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CineDarkBackground,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            if (!isPlayerScreen && userState.currentScreen !is Screen.Details) {
                Surface(
                    color = CineDarkBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cinetra Brand Logo & Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_cinetra_logo),
                                contentDescription = "Cinetra Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CINETRA",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = CineRedPrimary
                            )
                        }

                        // Search shortcut
                        IconButton(
                            onClick = { movieViewModel.navigateTo(Screen.Explore) },
                            modifier = Modifier.testTag("topbar_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }

                        // Watchlist shortcut
                        IconButton(
                            onClick = { movieViewModel.navigateTo(Screen.Watchlist) },
                            modifier = Modifier.testTag("topbar_watchlist_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (watchlist.isNotEmpty()) {
                                        Badge(containerColor = CineRedPrimary) {
                                            Text("${watchlist.size}", color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Watchlist",
                                    tint = if (watchlist.isNotEmpty()) CineGold else Color.White
                                )
                            }
                        }

                        // Dedicated Admin App launcher shortcut
                        IconButton(
                            onClick = {
                                val intent = Intent(context, AdminActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.testTag("topbar_admin_app_launcher")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Open Admin App",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!isPlayerScreen) {
                NavigationBar(
                    containerColor = CineDarkSurface,
                    contentColor = Color.White,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = userState.currentScreen is Screen.Home,
                        onClick = { movieViewModel.navigateTo(Screen.Home) },
                        icon = {
                            Icon(
                                imageVector = if (userState.currentScreen is Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CineRedPrimary,
                            indicatorColor = CineRedPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = userState.currentScreen is Screen.Explore,
                        onClick = { movieViewModel.navigateTo(Screen.Explore) },
                        icon = {
                            Icon(
                                imageVector = if (userState.currentScreen is Screen.Explore) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("Explore", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CineRedPrimary,
                            indicatorColor = CineRedPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    NavigationBarItem(
                        selected = userState.currentScreen is Screen.Watchlist,
                        onClick = { movieViewModel.navigateTo(Screen.Watchlist) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (watchlist.isNotEmpty()) {
                                        Badge(containerColor = CineRedPrimary) {
                                            Text("${watchlist.size}", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (userState.currentScreen is Screen.Watchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Watchlist"
                                )
                            }
                        },
                        label = { Text("Watchlist", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CineRedPrimary,
                            indicatorColor = CineRedPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_watchlist")
                    )

                    NavigationBarItem(
                        selected = userState.currentScreen is Screen.ContinueWatching,
                        onClick = { movieViewModel.navigateTo(Screen.ContinueWatching) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (continueWatching.isNotEmpty()) {
                                        Badge(containerColor = CineGold) {
                                            Text("${continueWatching.size}", color = Color.Black)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (userState.currentScreen is Screen.ContinueWatching) Icons.Filled.History else Icons.Outlined.History,
                                    contentDescription = "Watching"
                                )
                            }
                        },
                        label = { Text("History", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CineRedPrimary,
                            indicatorColor = CineRedPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_history")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = userState.currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        trending = userState.trending,
                        popularMovies = userState.popularMovies,
                        topRatedTv = userState.topRatedTv,
                        customMedia = userState.customMedia,
                        continueWatching = continueWatching,
                        announcements = activeAnnouncements,
                        isLoading = userState.isLoading,
                        isItemInWatchlist = { movieViewModel.isItemInWatchlist(it) },
                        onMediaClick = { movieViewModel.openDetails(it) },
                        onWatchClick = { movieViewModel.openPlayer(it) },
                        onWatchlistToggle = { movieViewModel.toggleWatchlist(it) },
                        onTrailerClick = { movieViewModel.openTrailer(it) },
                        onSeeAllClick = { movieViewModel.navigateTo(Screen.Explore) }
                    )
                }
                is Screen.Explore -> {
                    ExploreScreen(
                        searchQuery = userState.searchQuery,
                        onSearchQueryChange = { movieViewModel.onSearchQueryChange(it) },
                        selectedCategory = userState.selectedCategory,
                        onCategorySelect = { movieViewModel.selectCategory(it) },
                        selectedGenre = userState.selectedGenre,
                        onGenreSelect = { movieViewModel.selectGenre(it) },
                        sortBy = userState.sortBy,
                        onSortSelect = { movieViewModel.selectSortBy(it) },
                        allTitles = (userState.trending + userState.popularMovies + userState.topRatedTv + userState.customMedia).distinctBy { it.id },
                        searchResults = userState.searchResults,
                        isSearching = userState.isSearching,
                        onMediaClick = { movieViewModel.openDetails(it) },
                        searchHistory = searchHistory,
                        onDeleteSearchHistory = { movieViewModel.deleteSearchHistory(it) },
                        onClearSearchHistory = { movieViewModel.clearSearchHistory() }
                    )
                }
                is Screen.Watchlist -> {
                    WatchlistScreen(
                        watchlist = watchlist,
                        onMediaClick = { movieViewModel.openDetails(it) },
                        onRemoveFromWatchlist = { movieViewModel.toggleWatchlist(it) },
                        onExploreClick = { movieViewModel.navigateTo(Screen.Explore) }
                    )
                }
                is Screen.ContinueWatching -> {
                    ContinueWatchingScreen(
                        items = continueWatching,
                        onResumeWatching = { media, season, ep -> movieViewModel.openPlayer(media, season, ep) },
                        onRemoveItem = { movieViewModel.removeContinueWatching(it) },
                        onBrowseClick = { movieViewModel.navigateTo(Screen.Explore) }
                    )
                }
                is Screen.Details -> {
                    DetailsScreen(
                        media = screen.media,
                        isInWatchlist = movieViewModel.isItemInWatchlist(screen.media.id),
                        onWatchClick = { s, ep -> movieViewModel.openPlayer(screen.media, s, ep) },
                        onWatchlistToggle = { movieViewModel.toggleWatchlist(screen.media) },
                        onTrailerClick = { movieViewModel.openTrailer(screen.media) },
                        onBack = { movieViewModel.navigateBack() }
                    )
                }
                is Screen.Player -> {
                    StreamPlayerView(
                        media = screen.media,
                        currentSeason = userState.currentSeason,
                        currentEpisode = userState.currentEpisode,
                        selectedServer = userState.playerServer,
                        onServerSelect = { movieViewModel.switchPlayerServer(it) },
                        onEpisodeSelect = { s, ep -> movieViewModel.switchEpisode(s, ep) },
                        onBack = { movieViewModel.navigateBack() }
                    )
                }
                is Screen.Admin -> {
                    // If navigated to Admin via old route, launch AdminActivity
                    val intent = Intent(context, AdminActivity::class.java)
                    context.startActivity(intent)
                    movieViewModel.navigateBack()
                }
            }

            // Official YouTube Trailer Modal
            if (userState.isTrailerModalOpen && userState.trailerYoutubeKey != null) {
                TrailerModal(
                    youtubeKey = userState.trailerYoutubeKey!!,
                    title = userState.selectedMedia?.title ?: "Trailer",
                    onDismiss = { movieViewModel.closeTrailer() }
                )
            }
        }
    }
}
