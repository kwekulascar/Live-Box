
package com.livebox.streaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

private val Dark = Color(0xFF101018)
private val Red = Color(0xFFE50914)

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
    var selectedTab by remember { mutableStateOf("Home") }

    val tabs = listOf(
        "Home", "Movies", "Series", "Live TV", "Favorites"
    )

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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = selectedTab,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }

                item {
                    Text(
                        text = "Welcome to Live Box",
                        color = Color.LightGray,
                        fontSize = 18.sp
                    )
                }

                item {
                    Text(
                        text = "Your free entertainment starts here.",
                        color = Color.LightGray
                    )
                }

                item {
                    Text(
                        text = "Movies, series and live TV coming soon.",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
