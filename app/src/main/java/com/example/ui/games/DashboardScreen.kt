package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MinimalistThemeColors(
    val cardBg: Color,
    val iconBg: Color,
    val symbol: String
)

val minimalistThemeMap = mapOf(
    GameType.TIC_TAC_TOE to MinimalistThemeColors(Color(0xFFDDE1FF), Color(0xFF002B70), "#"),
    GameType.NUMBER_GUESSING to MinimalistThemeColors(Color(0xFFF2E0FF), Color(0xFF2D0050), "?"),
    GameType.ROCK_PAPER_SCISSORS to MinimalistThemeColors(Color(0xFFFFDAD6), Color(0xFF410002), "✊"),
    GameType.MEMORY_MATCH to MinimalistThemeColors(Color(0xFFD1F0DB), Color(0xFF00391C), "🎴"),
    GameType.SNAKE to MinimalistThemeColors(Color(0xFFE1E2EC), Color(0xFF191C23), "🐍"),
    GameType.WHACK_A_MOLE to MinimalistThemeColors(Color(0xFFFFEFD2), Color(0xFF281800), "🔨"),
    GameType.MATH_QUIZ to MinimalistThemeColors(Color(0xFFFFDBF0), Color(0xFF3D002E), "+"),
    GameType.WORD_SCRAMBLE to MinimalistThemeColors(Color(0xFFE8DEF8), Color(0xFF21005D), "A"),
    GameType.TAP_SPEED to MinimalistThemeColors(Color(0xFFD7E3FF), Color(0xFF001B3D), "⚡"),
    GameType.COIN_DICE to MinimalistThemeColors(Color(0xFFFFDCC0), Color(0xFF2F1500), "🎲")
)

@Composable
fun DashboardScreen(
    viewModel: GamesViewModel,
    onGameSelected: (GameType) -> Unit
) {
    val highScores by viewModel.highScores.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color(0xFFFDFBFF))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "GameBox",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1B1B1F),
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    )
                    Text(
                        "10 Mini Games",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF44464F).copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE1E2EC), CircleShape)
                        .testTag("dashboard_reset_scores")
                ) {
                    Icon(
                        Icons.Filled.DeleteSweep,
                        contentDescription = "Reset Scores",
                        tint = Color(0xFF1B1B1F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(72.dp)
                    .background(Color(0xFFF3F0F5))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(0.dp))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Games Tab (Active)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { /* Active */ }
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDDE1FF), RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.SportsEsports,
                            contentDescription = "Games",
                            tint = Color(0xFF001453),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Games",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001453)
                    )
                }

                // Stats Tab (Inactive)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { /* Aesthetic only */ }
                ) {
                    Icon(
                        Icons.Filled.ShowChart,
                        contentDescription = "Stats",
                        tint = Color(0xFF44464F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Stats",
                        fontSize = 11.sp,
                        color = Color(0xFF44464F)
                    )
                }

                // About Tab (Inactive)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { /* Aesthetic only */ }
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "About",
                        tint = Color(0xFF44464F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "About",
                        fontSize = 11.sp,
                        color = Color(0xFF44464F)
                    )
                }
            }
        },
        containerColor = Color(0xFFFDFBFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Clean Minimalist Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        color = Color(0xFFF3F0F5),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFC7C6CA),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "10-IN-1 MINI ARCADE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF002B70),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Time-Killer Games Box",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Tap on any card below to launch a casual game. Track and break your own high scores!",
                        fontSize = 12.sp,
                        color = Color(0xFF44464F),
                        lineHeight = 18.sp
                    )
                }
            }

            // Grid list of exactly 10 games
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(GameType.values()) { game ->
                    val score = highScores[game.name] ?: "No record"
                    val colors = minimalistThemeMap[game] ?: MinimalistThemeColors(Color(0xFFE1E2EC), Color(0xFF191C23), "?")

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.85f)
                            .clickable { onGameSelected(game) }
                            .testTag("game_card_${game.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Header row with Symbol Box and Difficulty Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(colors.iconBg, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = colors.symbol,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(colors.iconBg.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = game.difficulty,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.iconBg
                                    )
                                }
                            }

                            // Body details (Title and description)
                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = game.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B1B1F),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = game.description,
                                    fontSize = 10.sp,
                                    color = Color(0xFF44464F),
                                    lineHeight = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Footer containing current High Score
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = score,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.iconBg,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // High Scores Reset Dialog Confirmation
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F)) },
            text = { Text("This will permanently erase all high scores, streaks, and match records. Are you sure you want to proceed?", color = Color(0xFF44464F)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllScores()
                        showResetDialog = false
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("YES, RESET", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL", color = Color(0xFF44464F))
                }
            },
            containerColor = Color(0xFFF3F0F5)
        )
    }
}
