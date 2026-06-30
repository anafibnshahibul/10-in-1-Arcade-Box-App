package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RockPaperScissorsGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    val choices = listOf("Rock", "Paper", "Scissors")
    val emojis = mapOf("Rock" to "✊", "Paper" to "✋", "Scissors" to "✌️")
    val choiceColors = mapOf(
        "Rock" to Color(0xFFB3261E),     // Warm Red
        "Paper" to Color(0xFF002B70),    // Deep Indigo
        "Scissors" to Color(0xFFFF9F00)  // Elegant Tangerine
    )

    var playerChoice by remember { mutableStateOf<String?>(null) }
    var cpuChoice by remember { mutableStateOf<String?>(null) }
    var gameOutcome by remember { mutableStateOf<String>("Choose your weapon!") }
    var currentStreak by remember { mutableStateOf(0) }
    var isThinking by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val colors = minimalistThemeMap[GameType.ROCK_PAPER_SCISSORS]!!

    fun playRound(choice: String) {
        if (isThinking) return
        playerChoice = choice
        cpuChoice = null
        gameOutcome = "AI is thinking..."
        isThinking = true

        coroutineScope.launch {
            // Animate choices shuffling for a fun arcade feel!
            var shuffleIndex = 0
            repeat(10) {
                cpuChoice = choices[shuffleIndex]
                shuffleIndex = (shuffleIndex + 1) % choices.size
                delay(80)
            }

            val finalCpu = choices.random()
            cpuChoice = finalCpu
            isThinking = false

            val outcome = when {
                choice == finalCpu -> {
                    gameOutcome = "It's a Draw! 🤝"
                    "Draw"
                }
                (choice == "Rock" && finalCpu == "Scissors") ||
                (choice == "Paper" && finalCpu == "Rock") ||
                (choice == "Scissors" && finalCpu == "Paper") -> {
                    gameOutcome = "You Win! 🎉"
                    currentStreak++
                    viewModel.updateRpsStreak(currentStreak)
                    "Player"
                }
                else -> {
                    gameOutcome = "AI Wins! 🤖"
                    currentStreak = 0
                    "CPU"
                }
            }
        }
    }

    fun resetGame() {
        playerChoice = null
        cpuChoice = null
        gameOutcome = "Choose your weapon!"
        currentStreak = 0
        isThinking = false
    }

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("rps_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Rock Paper Scissors", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
            }
        },
        containerColor = Color(0xFFFDFBFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Streak Info Card
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
                        text = gameOutcome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.iconBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Streak: $currentStreak",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Duel Arena Visualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Card Area
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("YOU", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF44464F))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                            .border(1.dp, playerChoice?.let { choiceColors[it] } ?: Color(0xFFC7C6CA), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(targetState = playerChoice) { choice ->
                            if (choice != null) {
                                Text(emojis[choice] ?: "", fontSize = 56.sp)
                            } else {
                                Text("?", fontSize = 32.sp, color = Color(0xFF44464F), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // VS Indicator
                Text("VS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.iconBg)

                // CPU Card Area
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI CPU", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF44464F))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                            .border(1.dp, cpuChoice?.let { choiceColors[it] } ?: Color(0xFFC7C6CA), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(targetState = cpuChoice) { choice ->
                            if (choice != null) {
                                Text(emojis[choice] ?: "", fontSize = 56.sp)
                            } else {
                                Text("?", fontSize = 32.sp, color = Color(0xFF44464F), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Interactive Selector Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select your play:",
                    fontSize = 14.sp,
                    color = Color(0xFF44464F),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    choices.forEach { choice ->
                        val emoji = emojis[choice] ?: ""
                        val color = choiceColors[choice] ?: Color.White

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(6.dp)
                                .aspectRatio(1f)
                                .clickable(enabled = !isThinking) { playRound(choice) }
                                .border(
                                    width = if (playerChoice == choice) 2.dp else 1.dp,
                                    color = if (playerChoice == choice) color else Color(0xFFC7C6CA),
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .testTag("rps_choice_$choice")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(emoji, fontSize = 36.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = choice,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }

            // Restart Button
            IconButton(
                onClick = { resetGame() },
                modifier = Modifier.testTag("rps_reset")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Restart Game", tint = Color(0xFF44464F), modifier = Modifier.size(28.dp))
            }
        }
    }
}
