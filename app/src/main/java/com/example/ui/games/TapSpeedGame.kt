package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TapSpeedGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var tapsCount by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(10) }
    var gameActive by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Interactive Scale/Press Animation
    var isPressed by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Countdown Timer
    LaunchedEffect(key1 = gameActive) {
        if (gameActive) {
            while (timeLeft > 0 && gameActive) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0) {
                gameActive = false
                isGameOver = true
                viewModel.saveTapHighScore(tapsCount)
            }
        }
    }

    fun startNewGame() {
        tapsCount = 0
        timeLeft = 10
        isGameOver = false
        gameActive = true
    }

    fun handleButtonTap() {
        if (!gameActive) return
        tapsCount++
        coroutineScope.launch {
            isPressed = true
            delay(50)
            isPressed = false
        }
    }

    val rating = when {
        tapsCount == 0 -> "None"
        tapsCount < 30 -> "Snail 🐌"
        tapsCount < 50 -> "Friendly Sloth 🦥"
        tapsCount < 70 -> "Cheetah 🐆"
        tapsCount < 90 -> "Flash ⚡"
        else -> "Hyper Sonic Cosmic Entity 🛸"
    }

    val colors = minimalistThemeMap[GameType.TAP_SPEED]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("tap_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Tap Speed Test", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
            // HUD Row
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
                        text = "TAPS: $tapsCount",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "TIMER: ${timeLeft}s",
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft < 4) Color(0xFFB3261E) else colors.iconBg,
                        fontSize = 16.sp
                    )
                }
            }

            // Big Tap Core Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Massive click target
                    Box(
                        modifier = Modifier
                            .scale(scaleAnim)
                            .size(200.dp)
                            .background(
                                color = if (gameActive) colors.iconBg else Color(0xFFF3F0F5),
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (gameActive) Color.White.copy(alpha = 0.4f) else Color(0xFFC7C6CA),
                                shape = CircleShape
                            )
                            .clickable(
                                enabled = gameActive,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                handleButtonTap()
                            }
                            .testTag("tap_trigger"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (gameActive) "TAP!" else "LOCKED",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gameActive) Color.White else Color(0xFF44464F)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = if (gameActive) "Taps Per Second: ${(tapsCount / (11 - timeLeft).coerceAtLeast(1))} tps" else "Locked: click start below",
                        fontSize = 14.sp,
                        color = Color(0xFF44464F),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Overlay
                if (!gameActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isGameOver) "FINISHED!" else "TAP SPEED TEST",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.iconBg
                            )
                            Spacer(Modifier.height(12.dp))
                            if (isGameOver) {
                                Text(
                                    text = "Total Taps: $tapsCount",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B1B1F)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Your Level: $rating",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00391C)
                                )
                            } else {
                                Text(
                                    text = "Tap the button as fast as you can in 10s!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF44464F)
                                )
                            }
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = { startNewGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.testTag("tap_start")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("START SPEED TEST", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // High Score Banner
            Text(
                text = "Push your tapping fingers to the absolute speed limits!",
                fontSize = 12.sp,
                color = Color(0xFF44464F),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
