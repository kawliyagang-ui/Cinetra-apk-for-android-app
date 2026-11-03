package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminAnnouncementEntity
import com.example.data.model.CustomMediaEntity
import com.example.data.model.MediaItem
import com.example.data.model.TmdbSearchHistoryEntity
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AdminTab(val title: String) {
    ANALYTICS("Analytics"),
    ADD_CONTENT("Add Content"),
    MANAGE_CONTENT("Manage Items"),
    ANNOUNCEMENTS("Announcements")
}

data class AdminUiState(
    val isAuthenticated: Boolean = false,
    val authError: String? = null,
    val currentTab: AdminTab = AdminTab.ANALYTICS,
    
    // Add/Edit Content Form
    val formId: String = "",
    val formTitle: String = "",
    val formOverview: String = "",
    val formPosterUrl: String = "",
    val formBackdropUrl: String = "",
    val formMediaType: String = "movie", // "movie" or "tv"
    val formReleaseYear: String = "2024",
    val formVoteAverage: String = "8.2",
    val formGenres: String = "Action, Sci-Fi",
    val formVidlyId: String = "",
    val formSeasons: String = "1",
    val formEpisodes: String = "10",
    val isEditing: Boolean = false,
    val formStatusMessage: String? = null,
    val isFormSuccess: Boolean = false,

    // TMDb Auto-Fill Search
    val tmdbSearchQuery: String = "",
    val tmdbSearchResults: List<MediaItem> = emptyList(),
    val isTmdbSearching: Boolean = false,

    // Announcement Form
    val announcementTitle: String = "",
    val announcementMessage: String = "",
    val announcementType: String = "INFO", // "INFO", "WARNING", "UPDATE"
    val announcementStatusMessage: String? = null
)

class AdminViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val customMediaList: StateFlow<List<CustomMediaEntity>> = repository.customMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcementsList: StateFlow<List<AdminAnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tmdbSearchHistory: StateFlow<List<TmdbSearchHistoryEntity>> = repository.tmdbSearchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlistCount: StateFlow<Int> = MutableStateFlow(0)

    fun login(pin: String): Boolean {
        return if (pin.trim() == "admin123" || pin.trim() == "1234") {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                authError = null
            )
            true
        } else {
            _uiState.value = _uiState.value.copy(
                authError = "Invalid Passcode. (Hint: default is admin123)"
            )
            false
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            authError = null
        )
    }

    fun switchTab(tab: AdminTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab, formStatusMessage = null)
    }

    // TMDb Search for Auto-Fill
    fun onTmdbSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(tmdbSearchQuery = query)
    }

    fun searchTmdb(explicitQuery: String? = null) {
        val query = (explicitQuery ?: _uiState.value.tmdbSearchQuery).trim()
        if (query.isBlank()) return
        _uiState.value = _uiState.value.copy(tmdbSearchQuery = query)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTmdbSearching = true)
            // Persist the searched movie title in Room database (maintains top 5)
            repository.saveTmdbSearch(query)
            val results = repository.searchTitles(query)
            _uiState.value = _uiState.value.copy(
                tmdbSearchResults = results,
                isTmdbSearching = false
            )
        }
    }

    fun deleteSearchHistoryItem(query: String) {
        viewModelScope.launch {
            repository.deleteTmdbSearch(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearTmdbSearchHistory()
        }
    }


    fun autoFillFromTmdb(item: MediaItem) {
        _uiState.value = _uiState.value.copy(
            formTitle = item.title,
            formOverview = item.overview,
            formPosterUrl = item.fullPosterUrl,
            formBackdropUrl = item.fullBackdropUrl,
            formMediaType = item.mediaType,
            formReleaseYear = item.releaseDate.take(4).ifEmpty { "2024" },
            formVoteAverage = String.format("%.1f", item.voteAverage),
            formGenres = item.genres.joinToString(", "),
            formVidlyId = item.streamId,
            formSeasons = item.numberOfSeasons.toString(),
            formEpisodes = item.episodesPerSeason.toString(),
            formStatusMessage = "✓ Auto-filled details from TMDb for \"${item.title}\"!",
            isFormSuccess = true
        )
    }

    // Form inputs
    fun updateTitle(v: String) { _uiState.value = _uiState.value.copy(formTitle = v) }
    fun updateOverview(v: String) { _uiState.value = _uiState.value.copy(formOverview = v) }
    fun updatePosterUrl(v: String) { _uiState.value = _uiState.value.copy(formPosterUrl = v) }
    fun updateBackdropUrl(v: String) { _uiState.value = _uiState.value.copy(formBackdropUrl = v) }
    fun updateMediaType(v: String) { _uiState.value = _uiState.value.copy(formMediaType = v) }
    fun updateReleaseYear(v: String) { _uiState.value = _uiState.value.copy(formReleaseYear = v) }
    fun updateVoteAverage(v: String) { _uiState.value = _uiState.value.copy(formVoteAverage = v) }
    fun updateGenres(v: String) { _uiState.value = _uiState.value.copy(formGenres = v) }
    fun updateVidlyId(v: String) { _uiState.value = _uiState.value.copy(formVidlyId = v) }
    fun updateSeasons(v: String) { _uiState.value = _uiState.value.copy(formSeasons = v) }
    fun updateEpisodes(v: String) { _uiState.value = _uiState.value.copy(formEpisodes = v) }

    fun clearForm() {
        _uiState.value = _uiState.value.copy(
            formId = "",
            formTitle = "",
            formOverview = "",
            formPosterUrl = "",
            formBackdropUrl = "",
            formMediaType = "movie",
            formReleaseYear = "2024",
            formVoteAverage = "8.0",
            formGenres = "Action, Drama",
            formVidlyId = "",
            formSeasons = "1",
            formEpisodes = "10",
            isEditing = false,
            formStatusMessage = null
        )
    }

    fun saveContent() {
        val s = _uiState.value
        if (s.formTitle.isBlank() || s.formVidlyId.isBlank()) {
            _uiState.value = s.copy(
                formStatusMessage = "Title and Vidly ID / Link are required!",
                isFormSuccess = false
            )
            return
        }

        viewModelScope.launch {
            val entity = CustomMediaEntity(
                id = if (s.isEditing && s.formId.isNotBlank()) s.formId else "custom_${UUID.randomUUID().toString().take(8)}",
                title = s.formTitle.trim(),
                overview = s.formOverview.trim(),
                posterPath = s.formPosterUrl.trim().ifEmpty { "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500" },
                backdropPath = s.formBackdropUrl.trim(),
                mediaType = s.formMediaType,
                releaseDate = s.formReleaseYear.trim(),
                voteAverage = s.formVoteAverage.toDoubleOrNull() ?: 8.0,
                genres = s.formGenres.trim().ifEmpty { "Cinema" },
                vidlyId = s.formVidlyId.trim(),
                numberOfSeasons = s.formSeasons.toIntOrNull() ?: 1,
                episodesPerSeason = s.formEpisodes.toIntOrNull() ?: 10
            )

            if (s.isEditing) {
                repository.updateCustomMedia(entity)
                _uiState.value = _uiState.value.copy(
                    formStatusMessage = "Successfully updated \"${entity.title}\"!",
                    isFormSuccess = true
                )
            } else {
                repository.insertCustomMedia(entity)
                _uiState.value = _uiState.value.copy(
                    formStatusMessage = "Successfully added \"${entity.title}\" to Cinetra catalog!",
                    isFormSuccess = true
                )
            }
            clearForm()
        }
    }

    fun startEdit(item: CustomMediaEntity) {
        _uiState.value = _uiState.value.copy(
            formId = item.id,
            formTitle = item.title,
            formOverview = item.overview,
            formPosterUrl = item.posterPath,
            formBackdropUrl = item.backdropPath,
            formMediaType = item.mediaType,
            formReleaseYear = item.releaseDate,
            formVoteAverage = item.voteAverage.toString(),
            formGenres = item.genres,
            formVidlyId = item.vidlyId,
            formSeasons = item.numberOfSeasons.toString(),
            formEpisodes = item.episodesPerSeason.toString(),
            isEditing = true,
            currentTab = AdminTab.ADD_CONTENT,
            formStatusMessage = "Editing \"${item.title}\""
        )
    }

    fun deleteContent(id: String) {
        viewModelScope.launch {
            repository.deleteCustomMedia(id)
        }
    }

    // Announcement actions
    fun updateAnnouncementTitle(v: String) { _uiState.value = _uiState.value.copy(announcementTitle = v) }
    fun updateAnnouncementMessage(v: String) { _uiState.value = _uiState.value.copy(announcementMessage = v) }
    fun updateAnnouncementType(v: String) { _uiState.value = _uiState.value.copy(announcementType = v) }

    fun saveAnnouncement() {
        val s = _uiState.value
        if (s.announcementTitle.isBlank() || s.announcementMessage.isBlank()) {
            _uiState.value = s.copy(announcementStatusMessage = "Please enter both Title and Message.")
            return
        }
        viewModelScope.launch {
            repository.insertAnnouncement(
                title = s.announcementTitle.trim(),
                message = s.announcementMessage.trim(),
                type = s.announcementType
            )
            _uiState.value = _uiState.value.copy(
                announcementTitle = "",
                announcementMessage = "",
                announcementStatusMessage = "✓ Announcement published to user home screen!"
            )
        }
    }

    fun toggleAnnouncementActive(id: Int, active: Boolean) {
        viewModelScope.launch {
            repository.toggleAnnouncementActive(id, active)
        }
    }

    fun deleteAnnouncement(id: Int) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
        }
    }
}

class AdminViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
