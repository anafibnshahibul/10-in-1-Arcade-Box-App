package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SkipNext
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
fun WordScrambleGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    val wordsList = listOf(
        "ANDROID", "KOTLIN", "ARCADE", "FLIPPER", "CASINO",
        "MATRIX", "MOBILE", "VECTOR", "SHUFFLE", "CREATIVE",
        "GAMES", "DICE", "SNAKE", "MEMORY", "PUZZLE"
    )

    var originalWord by remember { mutableStateOf("") }
    var scrambledWord by remember { mutableStateOf("") }
    var userGuess by remember { mutableStateOf("") }
    var streak by remember { mutableStateOf(0) }
    var feedbackMessage by remember { mutableStateOf("Unscramble the letters!") }
    var isCorrectGuess by remember { mutableStateOf<Boolean?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun setupNewWord() {
        val word = wordsList.random()
        originalWord = word
        var scrambled: String
        do {
            scrambled = word.toList().shuffled().joinToString("")
        } while (scrambled == word && word.length > 1)
        scrambledWord = scrambled
        userGuess = ""
        isCorrectGuess = null
        feedbackMessage = "Unscramble the letters!"
    }

    fun checkGuess() {
        if (userGuess.trim().equals(originalWord, ignoreCase = true)) {
            feedbackMessage = "Correct! 🎉"
            isCorrectGuess = true
            streak++
            viewModel.updateWordStreak(streak)

            coroutineScope.launch {
                delay(1200)
                setupNewWord()
            }
        } else {
            feedbackMessage = "Try again! ❌ Keep guessing."
            isCorrectGuess = false
            streak = 0 // reset streak on failure
        }
    }

    fun skipWord() {
        streak = 0
        setupNewWord()
    }

    // Initialize first word
    LaunchedEffect(Unit) {
        setupNewWord()
    }

    val colors = minimalistThemeMap[GameType.WORD_SCRAMBLE]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("scramble_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Word Scramble", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
            // HUD Streak card
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
                        text = feedbackMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (isCorrectGuess) {
                            true -> Color(0xFF00391C)
                            false -> Color(0xFFB3261E)
                            else -> Color(0xFF1B1B1F)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.iconBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Streak: $streak",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Word Scramble Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Scrambled letters as styled individual neon tiles!
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    scrambledWord.forEach { letter ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.iconBg
                            )
                        }
                    }
                }
                Text(
                    text = "Scrambled Word",
                    fontSize = 12.sp,
                    color = Color(0xFF44464F),
                    fontWeight = FontWeight.Medium
                )
            }

            // Text Input Field & Submit Action
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = userGuess,
                    onValueChange = { if (isCorrectGuess != true) userGuess = it.uppercase() },
                    placeholder = { Text("TYPE YOUR SOLVE...", color = Color(0xFF74777F)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1B1B1F),
                        unfocusedTextColor = Color(0xFF1B1B1F),
                        focusedBorderColor = colors.iconBg,
                        unfocusedBorderColor = Color(0xFFC7C6CA),
                        cursorColor = colors.iconBg
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("scramble_input")
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skip Button
                    OutlinedButton(
                        onClick = { skipWord() },
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.iconBg),
                        border = BorderStroke(1.dp, Color(0xFFC7C6CA)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("scramble_skip")
                    ) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Skip")
                        Spacer(Modifier.width(8.dp))
                        Text("Skip Word", fontWeight = FontWeight.Bold)
                    }

                    // Submit Guess Button
                    Button(
                        onClick = { checkGuess() },
                        enabled = userGuess.isNotEmpty() && isCorrectGuess != true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.iconBg,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE1E2EC),
                            disabledContentColor = Color(0xFF44464F)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("scramble_submit")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Submit")
                        Spacer(Modifier.width(8.dp))
                        Text("Solve", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Helper line
            Text(
                text = "Solving correctly keeps streak alive | Skipping resets streak to 0",
                fontSize = 12.sp,
                color = Color(0xFF44464F),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
