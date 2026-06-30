package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WhackAMoleGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var activeMoleIndex by remember { mutableStateOf(-1) }
    var gameActive by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    // Countdown Timer Loop
    LaunchedEffect(key1 = gameActive) {
        if (gameActive) {
            while (timeLeft > 0 && gameActive) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0) {
                gameActive = false
                isGameOver = true
                viewModel.saveWhackHighScore(score)
            }
        }
    }

    // Mole Jumping Loop
    LaunchedEffect(key1 = gameActive) {
        if (gameActive) {
            while (timeLeft > 0 && gameActive) {
                // Determine random mole spawn
                activeMoleIndex = (0..8).random()
                // Moles speed up slightly as time runs out!
                val jumpInterval = when {
                    timeLeft > 20 -> 900L
                    timeLeft > 10 -> 750L
                    else -> 600L
                }
                delay(jumpInterval)
            }
            activeMoleIndex = -1
        }
    }

    fun startNewGame() {
        score = 0
        timeLeft = 30
        isGameOver = false
        gameActive = true
    }

    fun whackMole(index: Int) {
        if (!gameActive) return
        if (index == activeMoleIndex) {
            score += 10
            activeMoleIndex = -1 // remove mole immediately on correct tap
        } else {
            // small penalty for missing to encourage precision
            if (score >= 5) score -= 5
        }
    }

    val colors = minimalistThemeMap[GameType.WHACK_A_MOLE]!!

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color(0xFFFDFBFF))
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("whack_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Whack-A-Mole", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
            }
        },
        containerColor = Color(0xFFFDFBFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HUD
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.iconBg.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCORE: $score",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "TIME LEFT: ${timeLeft}s",
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft < 10) Color(0xFFB3261E) else colors.iconBg,
                        fontSize = 16.sp
                    )
                }
            }

            // Mole Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(28.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(9) { index ->
                        val isMoleActive = index == activeMoleIndex

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color(0xFFC7C6CA), CircleShape)
                                .clickable { whackMole(index) }
                                .testTag("whack_hole_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMoleActive) {
                                // A beautiful mole emoji with clean minimalist circular background
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(colors.iconBg, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🐹", fontSize = 36.sp)
                                }
                            }
                        }
                    }
                }

                // Overlay for game status
                if (!gameActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isGameOver) "GAME OVER" else "WHACK-A-MOLE",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGameOver) Color(0xFFB3261E) else colors.iconBg
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isGameOver) "Great Job! Final Score: $score" else "Tap the active moles quickly!",
                                fontSize = 14.sp,
                                color = Color(0xFF44464F)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { startNewGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.testTag("whack_start")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("START GAME", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Tutorial Line
            Text(
                text = "Hit correct moles (+10) | Avoid misses (-5)",
                fontSize = 12.sp,
                color = Color(0xFF44464F),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
