package com.livebox.streaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private val Dark = Color(0xFF101018)
private val Red = Color(0xFFE50914)

private data class Video(
    val title: String,
    val category: String,
    val description: String,
    val url: String
)

// Test clips hosted by MDN. Replace with content you have permission to distribute.
private val catalog = listOf(
    Video("Flower", "Nature", "A short flower video for testing playback.",
        "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"),
    Video("Flower (Replay)", "Short Films", "Replay the sample clip and test navigation.",
        "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LiveBoxApp() }
    }
}

@Composable
fun LiveBoxApp() {
    var selectedTab by remember { mutableStateOf("Home") }
    var search by remember { mutableStateOf("") }
    var selectedVideo by remember { mutableStateOf<Video?>(null) }
    val tabs = listOf("Home", "Movies", "Series", "Live TV", "Favorites")
    val filtered = catalog.filter {
        (it.title.contains(search, ignoreCase = true) ||
         it.category.contains(search, ignoreCase = true)) &&
        (selectedTab == "Home" || selectedTab == "Movies")
    }

    BackHandler(enabled = selectedVideo != null) { selectedVideo = null }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Dark).padding(16.dp)
        ) {
            Text("LIVE BOX", color = Red, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (selectedVideo != null) {
                Button(onClick = { selectedVideo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Red)) {
                    Text("Back to Live Box")
                }
                Text(selectedVideo!!.title, color = Color.White, fontSize = 22.sp)
                Spacer(Modifier.height(12.dp))
                VideoPlayer(selectedVideo!!.url)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tabs) { tab ->
                        Button(onClick = { selectedTab = tab },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == tab) Red else Color.DarkGray)) {
                            Text(tab)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    label = { Text("Search videos or categories") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray)
                )
                Spacer(Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text(if (selectedTab == "Home") "Featured Videos" else selectedTab,
                            color = Color.White, fontSize = 24.sp)
                    }
                    if (filtered.isEmpty()) {
                        item {
                            Text("No videos in this section yet.", color = Color.LightGray)
                        }
                    } else {
                        items(filtered) { video ->
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .background(Color(0xFF242430), RoundedCornerShape(12.dp))
                                    .clickable { selectedVideo = video }
                                    .padding(16.dp)
                            ) {
                                Text("▶  ${video.title}", color = Color.White,
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(video.category, color = Red)
                                Text(video.description, color = Color.LightGray)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { selectedVideo = video },
                                    colors = ButtonDefaults.buttonColors(containerColor = Red)) {
                                    Text("Play")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    var errorMessage by remember(url) { mutableStateOf<String?>(null) }
    val player = remember(context, url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                errorMessage = error.message ?: "Unable to play this video"
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }
    Column {
        AndroidView(factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        }, modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f))
        errorMessage?.let { Text(it, color = Color.Red) }
    }
}
