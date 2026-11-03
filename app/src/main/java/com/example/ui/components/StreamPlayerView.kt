package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.MediaItem
import com.example.data.model.PlayerServer
import com.example.ui.theme.CineDarkBackground
import com.example.ui.theme.CineDarkSurfaceElevated
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineRedPrimary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun StreamPlayerView(
    media: MediaItem,
    currentSeason: Int,
    currentEpisode: Int,
    selectedServer: PlayerServer,
    onServerSelect: (PlayerServer) -> Unit,
    onEpisodeSelect: (Int, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }

    val currentStreamUrl = remember(media, selectedServer, currentSeason, currentEpisode) {
        media.getStreamUrl(selectedServer, currentSeason, currentEpisode)
    }

    BackHandler {
        onBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.apply {
                stopLoading()
                loadUrl("about:blank")
                destroy()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineDarkBackground)
    ) {
        // Player Header Bar
        Surface(
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("player_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 6.dp)) {
                    Text(
                        text = media.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (media.mediaType == "tv") {
                        Text(
                            text = "Season $currentSeason • Episode $currentEpisode",
                            style = MaterialTheme.typography.bodySmall,
                            color = CineRedPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Now Streaming • ${selectedServer.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CineGold
                        )
                    }
                }

                IconButton(
                    onClick = { webViewInstance?.reload() },
                    modifier = Modifier.testTag("player_reload_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Player",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentStreamUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.testTag("player_open_browser_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open in browser",
                        tint = Color.White
                    )
                }
            }
        }

        // 16:9 Video Player Screen Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadsImagesAutomatically = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoadingPage = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoadingPage = false
                            }
                            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: return false
                                // Keep player within legitimate streaming / media domains
                                return !(url.contains("vidly") || url.contains("vidsrc") || url.contains("autoembed") || url.contains("multiembed") || url.contains("player") || url.contains("embed"))
                            }
                        }
                        webChromeClient = WebChromeClient()
                        val iframeHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                <style>
                                    * { margin: 0; padding: 0; box-sizing: border-box; }
                                    html, body { width: 100%; height: 100%; background: #000; overflow: hidden; }
                                    iframe { width: 100%; height: 100%; border: 0; }
                                </style>
                            </head>
                            <body>
                                <iframe 
                                    src="$currentStreamUrl" 
                                    allowfullscreen="true" 
                                    webkitallowfullscreen="true" 
                                    mozallowfullscreen="true"
                                    allow="autoplay; fullscreen; encrypted-media; picture-in-picture"
                                    referrerpolicy="origin">
                                </iframe>
                            </body>
                            </html>
                        """.trimIndent()
                        loadDataWithBaseURL(currentStreamUrl, iframeHtml, "text/html", "UTF-8", null)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    val iframeHtml = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                            <style>
                                * { margin: 0; padding: 0; box-sizing: border-box; }
                                html, body { width: 100%; height: 100%; background: #000; overflow: hidden; }
                                iframe { width: 100%; height: 100%; border: 0; }
                            </style>
                        </head>
                        <body>
                            <iframe 
                                src="$currentStreamUrl" 
                                allowfullscreen="true" 
                                webkitallowfullscreen="true" 
                                mozallowfullscreen="true"
                                allow="autoplay; fullscreen; encrypted-media; picture-in-picture"
                                referrerpolicy="origin">
                            </iframe>
                        </body>
                        </html>
                    """.trimIndent()
                    webView.loadDataWithBaseURL(currentStreamUrl, iframeHtml, "text/html", "UTF-8", null)
                }
            )

            if (isLoadingPage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = CineRedPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connecting to ${selectedServer.displayName}...",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Controls and Settings below player
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Server Switcher Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = CineRedPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Streaming Server (Switch if video fails to load)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerServer.values().forEach { server ->
                    val isSelected = selectedServer == server
                    FilterChip(
                        selected = isSelected,
                        onClick = { onServerSelect(server) },
                        label = {
                            Column {
                                Text(
                                    text = server.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = server.speed,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CineRedPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = CineDarkSurfaceElevated,
                            labelColor = Color.White.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.testTag("server_chip_${server.name}")
                    )
                }
            }

            // TV Series Season & Episode Selector
            if (media.mediaType == "tv") {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Episodes (Season $currentSeason)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    // Prev / Next quick jumps
                    Row {
                        IconButton(
                            onClick = {
                                if (currentEpisode > 1) {
                                    onEpisodeSelect(currentSeason, currentEpisode - 1)
                                }
                            },
                            enabled = currentEpisode > 1,
                            modifier = Modifier.size(32.dp).testTag("prev_episode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Episode",
                                tint = if (currentEpisode > 1) Color.White else Color.Gray
                            )
                        }

                        IconButton(
                            onClick = {
                                if (currentEpisode < media.episodesPerSeason) {
                                    onEpisodeSelect(currentSeason, currentEpisode + 1)
                                }
                            },
                            enabled = currentEpisode < media.episodesPerSeason,
                            modifier = Modifier.size(32.dp).testTag("next_episode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Episode",
                                tint = if (currentEpisode < media.episodesPerSeason) Color.White else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Seasons pills
                if (media.numberOfSeasons > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (s in 1..media.numberOfSeasons) {
                            val isSeasonSelected = s == currentSeason
                            FilterChip(
                                selected = isSeasonSelected,
                                onClick = { onEpisodeSelect(s, 1) },
                                label = { Text("Season $s", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CineDarkSurfaceElevated,
                                    selectedLabelColor = CineGold
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Episode Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (ep in 1..media.episodesPerSeason) {
                        val isEpSelected = ep == currentEpisode
                        Button(
                            onClick = { onEpisodeSelect(currentSeason, ep) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEpSelected) CineRedPrimary else CineDarkSurfaceElevated,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("episode_button_$ep")
                        ) {
                            Text(
                                text = "EP $ep",
                                fontSize = 12.sp,
                                fontWeight = if (isEpSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Information Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CineDarkSurfaceElevated)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Source Stream Link:",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentStreamUrl,
                        fontSize = 12.sp,
                        color = CineGold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cinetra embeds Vidly Pro and backup streaming providers. If ads appear in the embed, use an ad blocker or try server 2/3.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
