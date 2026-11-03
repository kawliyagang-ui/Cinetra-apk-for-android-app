package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminAnnouncementEntity
import com.example.data.model.ContinueWatchingEntity
import com.example.data.model.MediaItem
import com.example.data.model.PlayerServer
import com.example.data.model.TmdbSearchHistoryEntity
import com.example.data.model.WatchlistEntity
import com.example.data.model.toMediaItem
import com.example.data.repository.MovieRepository
import com.example.data.remote.CuratedCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
    object Watchlist : Screen()
    object ContinueWatching : Screen()
    data class Details(val media: MediaItem) : Screen()
    data class Player(val media: MediaItem, val season: Int = 1, val episode: Int = 1) : Screen()
    object Admin : Screen()
}

data class UserUiState(
    val currentScreen: Screen = Screen.Home,
    val backStack: List<Screen> = listOf(Screen.Home),
    val trending: List<MediaItem> = CuratedCatalog.trendingTitles,
    val popularMovies: List<MediaItem> = CuratedCatalog.popularMovies,
    val topRatedTv: List<MediaItem> = CuratedCatalog.topRatedSeries,
    val customMedia: List<MediaItem> = emptyList(),
    val searchResults: List<MediaItem> = emptyList(),
    val searchQuery: String = "",
    val selectedGenre: String = "All",
    val selectedCategory: String = "All", // "All", "movie", "tv"
    val sortBy: String = "popular", // "popular", "rating", "newest"
    val selectedMedia: MediaItem? = null,
    val playingMedia: MediaItem? = null,
    val currentSeason: Int = 1,
    val currentEpisode: Int = 1,
    val playerServer: PlayerServer = PlayerServer.VIDLY,
    val trailerYoutubeKey: String? = null,
    val isTrailerModalOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueWatching: StateFlow<List<ContinueWatchingEntity>> = repository.continueWatching
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAnnouncements: StateFlow<List<AdminAnnouncementEntity>> = repository.activeAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchHistory: StateFlow<List<TmdbSearchHistoryEntity>> = repository.tmdbSearchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initSeedDataIfEmpty()
        }
        loadHomeData()
        observeCustomMedia()
    }

    private fun observeCustomMedia() {
        viewModelScope.launch {
            repository.customMedia.collect { list ->
                _uiState.value = _uiState.value.copy(
                    customMedia = list.map { it.toMediaItem() }
                )
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                val trendingDeferred = async { repository.getTrending() }
                val popularDeferred = async { repository.getPopularMovies() }
                val tvDeferred = async { repository.getTopRatedTv() }

                val trending = trendingDeferred.await()
                val popular = popularDeferred.await()
                val tv = tvDeferred.await()

                _uiState.value = _uiState.value.copy(
                    trending = if (trending.isNotEmpty()) trending else _uiState.value.trending,
                    popularMovies = if (popular.isNotEmpty()) popular else _uiState.value.popularMovies,
                    topRatedTv = if (tv.isNotEmpty()) tv else _uiState.value.topRatedTv,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _uiState.value
        val newStack = if (screen is Screen.Home) {
            listOf(Screen.Home)
        } else {
            current.backStack + screen
        }
        _uiState.value = current.copy(
            currentScreen = screen,
            backStack = newStack
        )
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.backStack
        if (stack.size > 1) {
            val poppedStack = stack.dropLast(1)
            val previousScreen = poppedStack.last()
            _uiState.value = _uiState.value.copy(
                currentScreen = previousScreen,
                backStack = poppedStack
            )
            return true
        }
        return false
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(350) // Debounce
            _uiState.value = _uiState.value.copy(isSearching = true)
            if (query.trim().length >= 2) {
                repository.saveTmdbSearch(query.trim())
            }
            val results = repository.searchTitles(query)
            _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false)
        }
    }

    fun deleteSearchHistory(query: String) {
        viewModelScope.launch { repository.deleteTmdbSearch(query) }
    }

    fun clearSearchHistory() {
        viewModelScope.launch { repository.clearTmdbSearchHistory() }
    }


    fun selectGenre(genre: String) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun selectSortBy(sort: String) {
        _uiState.value = _uiState.value.copy(sortBy = sort)
    }

    fun openDetails(media: MediaItem) {
        _uiState.value = _uiState.value.copy(selectedMedia = media)
        navigateTo(Screen.Details(media))
        // Fetch trailer in background
        if (media.tmdbId != null && media.trailerYoutubeKey == null) {
            viewModelScope.launch {
                val key = repository.fetchTrailerYoutubeKey(media.tmdbId, media.mediaType)
                if (key != null) {
                    val updatedMedia = media.copy(trailerYoutubeKey = key)
                    _uiState.value = _uiState.value.copy(
                        selectedMedia = updatedMedia,
                        trailerYoutubeKey = key
                    )
                }
            }
        }
    }

    fun openPlayer(media: MediaItem, season: Int = 1, episode: Int = 1) {
        _uiState.value = _uiState.value.copy(
            playingMedia = media,
            currentSeason = season,
            currentEpisode = episode
        )
        // Automatically save to Continue Watching
        viewModelScope.launch {
            repository.saveContinueWatching(media, season, episode, progressPercent = 35)
        }
        navigateTo(Screen.Player(media, season, episode))
    }

    fun switchPlayerServer(server: PlayerServer) {
        _uiState.value = _uiState.value.copy(playerServer = server)
    }

    fun switchEpisode(season: Int, episode: Int) {
        val media = _uiState.value.playingMedia ?: return
        _uiState.value = _uiState.value.copy(
            currentSeason = season,
            currentEpisode = episode
        )
        viewModelScope.launch {
            repository.saveContinueWatching(media, season, episode, progressPercent = 10)
        }
    }

    fun openTrailer(media: MediaItem) {
        _uiState.value = _uiState.value.copy(
            trailerYoutubeKey = media.trailerYoutubeKey ?: "Way9Dexny3w",
            isTrailerModalOpen = true
        )
        if (media.tmdbId != null && media.trailerYoutubeKey == null) {
            viewModelScope.launch {
                val key = repository.fetchTrailerYoutubeKey(media.tmdbId, media.mediaType)
                if (key != null) {
                    _uiState.value = _uiState.value.copy(trailerYoutubeKey = key)
                }
            }
        }
    }

    fun closeTrailer() {
        _uiState.value = _uiState.value.copy(isTrailerModalOpen = false)
    }

    fun toggleWatchlist(media: MediaItem) {
        viewModelScope.launch {
            repository.toggleWatchlist(media)
        }
    }

    fun isItemInWatchlist(id: String): Boolean {
        return watchlist.value.any { it.id == id }
    }

    fun removeContinueWatching(id: String) {
        viewModelScope.launch {
            repository.removeContinueWatching(id)
        }
    }
}

class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
