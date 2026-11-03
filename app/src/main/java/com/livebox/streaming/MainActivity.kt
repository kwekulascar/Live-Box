package com.livebox.streaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private val Dark = Color(0xFF101018)
private val Red = Color(0xFFE50914)

private const val SAMPLE_VIDEO =
    "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveBoxApp()
        }
    }
}

@Composable
fun LiveBoxApp() {
    var selectedTab by remember {
        mutableStateOf("Home")
    }

    var playing by remember {
        mutableStateOf(false)
    }

    val tabs = listOf(
        "Home", "Movies", "Series",
        "Live TV", "Favorites"
    )

    BackHandler(enabled = playing) {
        playing = false
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Dark)
                .padding(16.dp)
        ) {
            Text(
                text = "LIVE BOX",
                color = Red,
                fontSize = 30.sp
            )

            Spacer(Modifier.height(16.dp))

            if (playing) {
                Button(
                    onClick = { playing = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red
                    )
                ) {
                    Text("Back to Live Box")
                }

                Spacer(Modifier.height(12.dp))

                VideoPlayer(SAMPLE_VIDEO)
            } else {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { tab ->
                        Button(
                            onClick = { selectedTab = tab },
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedTab == tab) Red
                                    else Color.DarkGray
                            )
                        ) {
                            Text(tab)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = selectedTab,
                    color = Color.White,
                    fontSize = 24.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Welcome to Live Box",
                    color = Color.LightGray,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { playing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red
                    )
                ) {
                    Text("▶ Play Test Video")
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Movies, series and live TV coming soon.",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val player = remember(context, url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}
