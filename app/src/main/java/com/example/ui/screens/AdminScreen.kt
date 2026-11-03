package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdminAnnouncementEntity
import com.example.data.model.CustomMediaEntity
import com.example.data.model.MediaItem
import com.example.data.model.toMediaItem
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CineDarkBackground
import com.example.ui.theme.CineDarkSurface
import com.example.ui.theme.CineDarkSurfaceElevated
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineRedPrimary
import com.example.ui.theme.CineSuccess
import com.example.ui.viewmodel.AdminTab
import com.example.ui.viewmodel.AdminUiState
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    adminState: AdminUiState,
    customMediaList: List<CustomMediaEntity>,
    announcementsList: List<AdminAnnouncementEntity>,
    totalCatalogCount: Int,
    onPreviewMedia: (MediaItem) -> Unit,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onExitAdmin()
    }

    if (!adminState.isAuthenticated) {
        AdminLoginGate(
            authError = adminState.authError,
            onLogin = { pin -> viewModel.login(pin) },
            onCancel = onExitAdmin
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineDarkBackground)
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CineDarkSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onExitAdmin,
                modifier = Modifier.testTag("admin_exit_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit to User App",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_cinetra_logo),
                contentDescription = "Cinetra Logo",
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CineRedPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ADMIN PANEL",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cinetra Studio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Text(
                    text = "Catalog & Stream Server Management",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onExitAdmin,
                modifier = Modifier.testTag("admin_open_main_app_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "Open User App",
                    tint = CineGold
                )
            }

            IconButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.testTag("admin_logout_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.Gray
                )
            }
        }

        // Tab Navigation
        TabRow(
            selectedTabIndex = adminState.currentTab.ordinal,
            containerColor = CineDarkSurfaceElevated,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[adminState.currentTab.ordinal]),
                    color = CineRedPrimary
                )
            }
        ) {
            AdminTab.values().forEach { tab ->
                val isSelected = adminState.currentTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.switchTab(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_${tab.name}")
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (adminState.currentTab) {
                AdminTab.ANALYTICS -> AdminAnalyticsTab(
                    totalCatalogCount = totalCatalogCount,
                    customMediaCount = customMediaList.size,
                    announcementsCount = announcementsList.count { it.active },
                    onNavigateToAdd = { viewModel.switchTab(AdminTab.ADD_CONTENT) },
                    onNavigateToManage = { viewModel.switchTab(AdminTab.MANAGE_CONTENT) }
                )
                AdminTab.ADD_CONTENT -> AdminAddContentTab(
                    state = adminState,
                    viewModel = viewModel,
                    onPreviewStream = {
                        val dummy = MediaItem(
                            id = adminState.formId.ifBlank { "test_stream" },
                            title = adminState.formTitle.ifBlank { "Stream Preview" },
                            overview = adminState.formOverview,
                            posterPath = adminState.formPosterUrl,
                            backdropPath = adminState.formBackdropUrl,
                            mediaType = adminState.formMediaType,
                            vidlyId = adminState.formVidlyId
                        )
                        onPreviewMedia(dummy)
                    }
                )
                AdminTab.MANAGE_CONTENT -> AdminManageContentTab(
                    items = customMediaList,
                    onEdit = { viewModel.startEdit(it) },
                    onDelete = { viewModel.deleteContent(it) },
                    onPreview = { onPreviewMedia(it.toMediaItem()) },
                    onAddNew = {
                        viewModel.clearForm()
                        viewModel.switchTab(AdminTab.ADD_CONTENT)
                    }
                )
                AdminTab.ANNOUNCEMENTS -> AdminAnnouncementsTab(
                    state = adminState,
                    announcements = announcementsList,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun AdminLoginGate(
    authError: String?,
    onLogin: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CineDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CineBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(CineRedPrimary.copy(alpha = 0.15f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CineRedPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin Authentication",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Text(
                    text = "Enter the administrator passcode to manage content and streaming settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Admin Passcode") },
                    placeholder = { Text("admin123") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CineRedPrimary,
                        unfocusedBorderColor = CineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (authError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = authError,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Default passcode is: admin123",
                        color = CineGold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).testTag("admin_login_cancel_button")
                    ) {
                        Text("Back to App", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { onLogin(pinText) },
                        colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                        modifier = Modifier.weight(1f).testTag("admin_login_submit_button")
                    ) {
                        Text("Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAnalyticsTab(
    totalCatalogCount: Int,
    customMediaCount: Int,
    announcementsCount: Int,
    onNavigateToAdd: () -> Unit,
    onNavigateToManage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Platform Analytics",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Overview of streaming infrastructure and catalog metrics.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Metric Cards Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AnalyticsStatCard(
                title = "Total Catalog",
                value = "$totalCatalogCount",
                icon = Icons.Default.Movie,
                accentColor = CineRedPrimary,
                modifier = Modifier.weight(1f)
            )
            AnalyticsStatCard(
                title = "Admin Added",
                value = "$customMediaCount",
                icon = Icons.Default.Add,
                accentColor = CineGold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AnalyticsStatCard(
                title = "Active Notices",
                value = "$announcementsCount",
                icon = Icons.Default.Campaign,
                accentColor = CineSuccess,
                modifier = Modifier.weight(1f)
            )
            AnalyticsStatCard(
                title = "Vidly Streams",
                value = "100%",
                icon = Icons.Default.BarChart,
                accentColor = Color(0xFF60A5FA),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System Health Status Box
        Card(
            colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Streaming Services Health",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                ServiceStatusRow(service = "Vidly Pro Gateway (https://vidly.pro/movie/{id})", status = "ONLINE - LOW LATENCY", isOnline = true)
                Spacer(modifier = Modifier.height(8.dp))
                ServiceStatusRow(service = "Backup Servers (VidSrc / AutoEmbed / MultiEmbed)", status = "ACTIVE & READY", isOnline = true)
                Spacer(modifier = Modifier.height(8.dp))
                ServiceStatusRow(service = "TMDb API v3 Gateway", status = "CONNECTED", isOnline = true)
                Spacer(modifier = Modifier.height(8.dp))
                ServiceStatusRow(service = "Room SQLite Local Database", status = "HEALTHY & SYNCED", isOnline = true)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onNavigateToAdd,
                colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("quick_add_content_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New Movie/Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateToManage,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("quick_manage_button")
            ) {
                Text("Manage Existing", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = Color.Gray)
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

@Composable
private fun ServiceStatusRow(service: String, status: String, isOnline: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isOnline) CineSuccess else Color.Red, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = service, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOnline) CineSuccess else Color.Red
        )
    }
}

@Composable
private fun AdminAddContentTab(
    state: AdminUiState,
    viewModel: AdminViewModel,
    onPreviewStream: () -> Unit
) {
    val searchHistory by viewModel.tmdbSearchHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section 1: TMDb Search & Auto-Fill
        Card(
            colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CineGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TMDb Auto-Fill Search",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Text(
                    text = "Type any movie or show title to fetch metadata, posters, and auto-populate the form.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state.tmdbSearchQuery,
                        onValueChange = { viewModel.onTmdbSearchQueryChange(it) },
                        placeholder = { Text("Search TMDb (e.g., Gladiator II, Arcane)...", fontSize = 12.sp, color = Color.Gray) },
                        singleLine = true,
                        trailingIcon = {
                            if (state.tmdbSearchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onTmdbSearchQueryChange("") },
                                    modifier = Modifier.testTag("admin_clear_tmdb_input")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.searchTmdb() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CineRedPrimary,
                            unfocusedBorderColor = CineBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_tmdb_search_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.searchTmdb() },
                        colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_tmdb_search_button")
                    ) {
                        if (state.isTmdbSearching) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Search", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // TMDb Search History (Last 5 searched movie titles saved in Room DB)
                if (searchHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Search History",
                                tint = CineGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recent Searches (${searchHistory.size}/5):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CineGold
                            )
                        }
                        Text(
                            text = "Clear All",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { viewModel.clearSearchHistory() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("clear_tmdb_search_history")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(searchHistory, key = { it.query }) { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CineDarkSurface)
                                    .border(1.dp, CineBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.searchTmdb(item.query)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("recent_tmdb_chip_${item.query}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = item.query,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable {
                                                viewModel.deleteSearchHistoryItem(item.query)
                                            }
                                            .testTag("delete_recent_tmdb_${item.query}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove ${item.query}",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // TMDb results list
                if (state.tmdbSearchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Tap to Auto-Fill Form:", fontSize = 11.sp, color = CineGold, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.tmdbSearchResults, key = { it.id }) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CineDarkSurface),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(140.dp)
                                    .clickable { viewModel.autoFillFromTmdb(item) }
                                    .testTag("tmdb_result_${item.id}")
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(90.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    ) {
                                        AsyncImage(
                                            model = item.fullPosterUrl,
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.mediaType.uppercase()} • ${item.releaseDate.take(4)}",
                                        fontSize = 9.sp,
                                        color = CineGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Movie / Series Form
        Text(
            text = if (state.isEditing) "Edit Catalog Item" else "Manual Addition Form",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        if (state.formStatusMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (state.isFormSuccess) CineSuccess.copy(alpha = 0.2f) else CineRedPrimary.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = state.formStatusMessage,
                    color = if (state.isFormSuccess) CineSuccess else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title
        OutlinedTextField(
            value = state.formTitle,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Title *") },
            placeholder = { Text("e.g. Inception") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_title")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category (Movie / TV)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Category:", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = state.formMediaType == "movie",
                onClick = { viewModel.updateMediaType("movie") },
                label = { Text("Movie") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CineRedPrimary, selectedLabelColor = Color.White),
                modifier = Modifier.testTag("admin_cat_movie")
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = state.formMediaType == "tv",
                onClick = { viewModel.updateMediaType("tv") },
                label = { Text("TV Series") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CineRedPrimary, selectedLabelColor = Color.White),
                modifier = Modifier.testTag("admin_cat_tv")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vidly ID (Maps to https://vidly.pro/movie/{id})
        OutlinedTextField(
            value = state.formVidlyId,
            onValueChange = { viewModel.updateVidlyId(it) },
            label = { Text("Vidly ID or Link *") },
            placeholder = { Text("e.g. 27205 (TMDb ID or custom vidly id)") },
            supportingText = { Text("Mapped to: https://vidly.pro/movie/${state.formVidlyId.ifBlank { "{id}" }}", color = CineGold, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_vidly_id")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Year & Rating
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.formReleaseYear,
                onValueChange = { viewModel.updateReleaseYear(it) },
                label = { Text("Release Year") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("admin_form_year")
            )
            OutlinedTextField(
                value = state.formVoteAverage,
                onValueChange = { viewModel.updateVoteAverage(it) },
                label = { Text("Rating (0 - 10)") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("admin_form_rating")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Genres
        OutlinedTextField(
            value = state.formGenres,
            onValueChange = { viewModel.updateGenres(it) },
            label = { Text("Genres (Comma separated)") },
            placeholder = { Text("Action, Sci-Fi, Thriller") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_genres")
        )

        if (state.formMediaType == "tv") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.formSeasons,
                    onValueChange = { viewModel.updateSeasons(it) },
                    label = { Text("Number of Seasons") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("admin_form_seasons")
                )
                OutlinedTextField(
                    value = state.formEpisodes,
                    onValueChange = { viewModel.updateEpisodes(it) },
                    label = { Text("Episodes per Season") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("admin_form_episodes")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Poster URL
        OutlinedTextField(
            value = state.formPosterUrl,
            onValueChange = { viewModel.updatePosterUrl(it) },
            label = { Text("Poster Image URL") },
            placeholder = { Text("https://image.tmdb.org/t/p/w500/... or https://...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_poster_url")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Backdrop URL
        OutlinedTextField(
            value = state.formBackdropUrl,
            onValueChange = { viewModel.updateBackdropUrl(it) },
            label = { Text("Backdrop Image URL (Optional)") },
            placeholder = { Text("https://image.tmdb.org/t/p/w1280/...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_backdrop_url")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Overview
        OutlinedTextField(
            value = state.formOverview,
            onValueChange = { viewModel.updateOverview(it) },
            label = { Text("Plot Overview / Description") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("admin_form_overview")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Actions
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { viewModel.clearForm() },
                modifier = Modifier.weight(0.7f).testTag("admin_clear_form_button")
            ) {
                Text("Reset", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (state.formVidlyId.isNotBlank()) {
                OutlinedButton(
                    onClick = onPreviewStream,
                    modifier = Modifier.weight(0.9f).testTag("admin_test_stream_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CineGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Stream", color = CineGold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = { viewModel.saveContent() },
                colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1.2f).testTag("admin_save_content_button")
            ) {
                Text(
                    text = if (state.isEditing) "Update Title" else "Save to Catalog",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun AdminManageContentTab(
    items: List<CustomMediaEntity>,
    onEdit: (CustomMediaEntity) -> Unit,
    onDelete: (String) -> Unit,
    onPreview: (CustomMediaEntity) -> Unit,
    onAddNew: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = items.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.vidlyId.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom Media Database (${items.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Button(
                onClick = onAddNew,
                colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp).testTag("admin_manage_add_new_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Title", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter custom titles...", fontSize = 12.sp, color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("manage_search_filter")
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (items.isEmpty()) "No custom movies or series added yet.\nUse 'Add Content' tab to add items!" else "No matching items found.",
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("manage_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 50.dp, height = 75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                AsyncImage(
                                    model = item.posterPath,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.mediaType.uppercase()} • Year: ${item.releaseDate} • Rating: ${item.voteAverage}",
                                    fontSize = 11.sp,
                                    color = CineGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Vidly ID: ${item.vidlyId}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // Actions
                            IconButton(
                                onClick = { onPreview(item) },
                                modifier = Modifier.size(32.dp).testTag("preview_manage_${item.id}")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = CineGold)
                            }

                            IconButton(
                                onClick = { onEdit(item) },
                                modifier = Modifier.size(32.dp).testTag("edit_manage_${item.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }

                            IconButton(
                                onClick = { onDelete(item.id) },
                                modifier = Modifier.size(32.dp).testTag("delete_manage_${item.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAnnouncementsTab(
    state: AdminUiState,
    announcements: List<AdminAnnouncementEntity>,
    viewModel: AdminViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Announcement & Notice Banner Management",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Broadcast alerts, server updates, and notices directly to users on the app home screen.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Create Announcement Form
        Card(
            colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Text(
                    text = "Publish New Announcement",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )

                if (state.announcementStatusMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.announcementStatusMessage,
                        color = CineSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.announcementTitle,
                    onValueChange = { viewModel.updateAnnouncementTitle(it) },
                    label = { Text("Banner Title") },
                    placeholder = { Text("e.g. 4K Streaming Servers Online!") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("announcement_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.announcementMessage,
                    onValueChange = { viewModel.updateAnnouncementMessage(it) },
                    label = { Text("Notice Message") },
                    placeholder = { Text("e.g. We added 3 high-speed backup players for seamless movie playback.") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("announcement_message_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Banner Type:", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    listOf("INFO", "UPDATE", "WARNING").forEach { type ->
                        FilterChip(
                            selected = state.announcementType == type,
                            onClick = { viewModel.updateAnnouncementType(type) },
                            label = { Text(type, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineRedPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.padding(end = 4.dp).testTag("announcement_type_$type")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveAnnouncement() },
                    colors = ButtonDefaults.buttonColors(containerColor = CineRedPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("publish_announcement_button")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish to Users", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Existing Announcements List
        Text(
            text = "Active & Past Announcements (${announcements.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        announcements.forEach { ann ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CineDarkSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("announcement_item_${ann.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when (ann.type.uppercase()) {
                                            "WARNING" -> Color(0xFFD97706)
                                            "UPDATE" -> CineSuccess
                                            else -> Color(0xFF2563EB)
                                        }
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(text = ann.type, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ann.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ann.message,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Active Toggle
                    Switch(
                        checked = ann.active,
                        onCheckedChange = { viewModel.toggleAnnouncementActive(ann.id, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CineSuccess
                        ),
                        modifier = Modifier.testTag("toggle_announcement_${ann.id}")
                    )

                    IconButton(
                        onClick = { viewModel.deleteAnnouncement(ann.id) },
                        modifier = Modifier.size(32.dp).testTag("delete_announcement_${ann.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
