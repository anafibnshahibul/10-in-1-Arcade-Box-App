package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

@Composable
fun NumberGuessingGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var secretCode by remember { mutableStateOf((1..100).random()) }
    var currentGuess by remember { mutableStateOf(50f) }
    var attempts by remember { mutableStateOf(0) }
    var gameWon by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("Take a guess between 1 and 100!") }
    var pastGuesses by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    fun submitGuess() {
        if (gameWon) return

        val guessValue = currentGuess.toInt()
        attempts++

        val result = when {
            guessValue > secretCode -> {
                feedbackMessage = "Too High! 📈 Try a lower number."
                "Too High"
            }
            guessValue < secretCode -> {
                feedbackMessage = "Too Low! 📉 Try a higher number."
                "Too Low"
            }
            else -> {
                feedbackMessage = "Bingo! 🎉 You found it in $attempts attempts!"
                gameWon = true
                viewModel.saveNumberGuessingResult(attempts)
                "Found It!"
            }
        }

        pastGuesses = pastGuesses + (guessValue to result)
    }

    fun resetGame() {
        secretCode = (1..100).random()
        currentGuess = 50f
        attempts = 0
        gameWon = false
        feedbackMessage = "Take a guess between 1 and 100!"
        pastGuesses = emptyList()
    }

    val colors = minimalistThemeMap[GameType.NUMBER_GUESSING]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("num_guess_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Number Guessing", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
            // Feedback Card
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.iconBg.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (gameWon) "VICTORY" else "ATTEMPTS: $attempts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.iconBg,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = feedbackMessage,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F),
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }

            // Current Guess Preview
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentGuess.toInt().toString(),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.iconBg
                )
                Text(
                    text = "Current Guess Selection",
                    fontSize = 12.sp,
                    color = Color(0xFF44464F),
                    fontWeight = FontWeight.Medium
                )
            }

            // Interactive Selector Slider
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = currentGuess,
                    onValueChange = { if (!gameWon) currentGuess = it },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.iconBg,
                        activeTrackColor = colors.iconBg,
                        inactiveTrackColor = Color(0xFFE1E2EC)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp).testTag("num_guess_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1", color = Color(0xFF44464F), fontSize = 12.sp)
                    Text("50", color = Color(0xFF44464F), fontSize = 12.sp)
                    Text("100", color = Color(0xFF44464F), fontSize = 12.sp)
                }
            }

            // Controls & Guess History
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { submitGuess() },
                    enabled = !gameWon,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.iconBg,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE1E2EC),
                        disabledContentColor = Color(0xFF44464F)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(52.dp)
                        .testTag("num_guess_submit")
                ) {
                    Text(
                        text = if (gameWon) "MATCH FINISHED" else "SUBMIT GUESS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // History Row
                if (pastGuesses.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "History of Guesses",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1F),
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(pastGuesses) { (guess, desc) ->
                                val (color, labelColor) = when (desc) {
                                    "Too High" -> Pair(Color(0xFFFFDAD6), Color(0xFFB3261E)) // Warm Red
                                    "Too Low" -> Pair(Color(0xFFD7E3FF), Color(0xFF002B70))  // Cool Blue
                                    else -> Pair(Color(0xFFD1F0DB), Color(0xFF00391C))        // Sage Green
                                }
                                Box(
                                    modifier = Modifier
                                        .background(color, RoundedCornerShape(12.dp))
                                        .border(1.dp, labelColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "$guess ($desc)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = labelColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reset Game
            IconButton(
                onClick = { resetGame() },
                modifier = Modifier.testTag("num_guess_reset")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Restart Game", tint = Color(0xFF44464F), modifier = Modifier.size(28.dp))
            }
        }
    }
}
