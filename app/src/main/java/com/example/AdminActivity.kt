package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.MediaItem
import com.example.data.model.PlayerServer
import com.example.data.repository.MovieRepository
import com.example.ui.components.StreamPlayerView
import com.example.ui.screens.AdminScreen
import com.example.ui.theme.CineDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AdminViewModelFactory

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CinetraAdminApp(
                    onExit = { finish() },
                    onOpenMainApp = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun CinetraAdminApp(
    onExit: () -> Unit,
    onOpenMainApp: () -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = MovieRepository(database.mediaDao())

    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(repository)
    )

    val adminState by adminViewModel.uiState.collectAsState()
    val customMediaList by adminViewModel.customMediaList.collectAsState()
    val announcementsList by adminViewModel.announcementsList.collectAsState()

    var previewMediaItem by remember { mutableStateOf<MediaItem?>(null) }
    var previewServer by remember { mutableStateOf(PlayerServer.VIDLY) }
    var previewSeason by remember { mutableStateOf(1) }
    var previewEpisode by remember { mutableStateOf(1) }

    BackHandler {
        if (previewMediaItem != null) {
            previewMediaItem = null
        } else {
            onExit()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CineDarkBackground,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CineDarkBackground)
        ) {
            if (previewMediaItem != null) {
                StreamPlayerView(
                    media = previewMediaItem!!,
                    currentSeason = previewSeason,
                    currentEpisode = previewEpisode,
                    selectedServer = previewServer,
                    onServerSelect = { previewServer = it },
                    onEpisodeSelect = { s, ep ->
                        previewSeason = s
                        previewEpisode = ep
                    },
                    onBack = { previewMediaItem = null }
                )
            } else {
                AdminScreen(
                    viewModel = adminViewModel,
                    adminState = adminState,
                    customMediaList = customMediaList,
                    announcementsList = announcementsList,
                    totalCatalogCount = (15 + customMediaList.size),
                    onPreviewMedia = { item ->
                        previewMediaItem = item
                        previewSeason = 1
                        previewEpisode = 1
                    },
                    onExitAdmin = onExit
                )
            }
        }
    }
}
