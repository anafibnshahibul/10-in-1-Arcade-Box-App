package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MathQuizGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var gameActive by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf(0) }
    var options by remember { mutableStateOf(listOf<Int>()) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var isAnsweringLocked by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun generateQuestion() {
        val operations = listOf("+", "-", "*")
        val op = operations.random()
        val a: Int
        val b: Int
        val ans = when (op) {
            "+" -> {
                a = (1..50).random()
                b = (1..50).random()
                questionText = "$a + $b = ?"
                a + b
            }
            "-" -> {
                a = (1..50).random()
                b = (1..a).random() // guarantee positive results for simplicity
                questionText = "$a - $b = ?"
                a - b
            }
            else -> {
                a = (2..12).random()
                b = (2..12).random()
                questionText = "$a × $b = ?"
                a * b
            }
        }
        correctAnswer = ans

        // Generate decoys (unique options)
        val decoys = mutableSetOf<Int>()
        while (decoys.size < 3) {
            val offset = (-10..10).random()
            val decoy = ans + offset
            if (decoy != ans && decoy > 0) {
                decoys.add(decoy)
            }
        }
        options = (decoys + ans).toList().shuffled()
        selectedAnswer = null
        isAnsweringLocked = false
    }

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
                viewModel.saveMathHighScore(score)
            }
        }
    }

    fun startNewGame() {
        score = 0
        timeLeft = 30
        isGameOver = false
        generateQuestion()
        gameActive = true
    }

    fun submitAnswer(option: Int) {
        if (isAnsweringLocked || !gameActive) return
        isAnsweringLocked = true
        selectedAnswer = option

        coroutineScope.launch {
            if (option == correctAnswer) {
                score += 10
            } else {
                if (score >= 5) score -= 5
            }
            delay(400) // brief delay to show success/error highlight
            generateQuestion()
        }
    }

    val colors = minimalistThemeMap[GameType.MATH_QUIZ]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("math_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Quick Math Quiz", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
                        text = "TIME: ${timeLeft}s",
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft < 10) Color(0xFFB3261E) else colors.iconBg,
                        fontSize = 16.sp
                    )
                }
            }

            // Question Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SOLVE THIS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF44464F),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (gameActive) questionText else "Are you ready?",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F),
                        textAlign = TextAlign.Center
                    )
                }

                // Overlay
                if (!gameActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isGameOver) "TIME'S UP" else "QUICK MATH QUIZ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGameOver) Color(0xFFB3261E) else colors.iconBg
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isGameOver) "Your Final Score: $score" else "30 seconds to solve equations!",
                                fontSize = 14.sp,
                                color = Color(0xFF44464F)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { startNewGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.testTag("math_start")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("START QUIZ", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Choice Grid (4 options)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowOptions.forEach { option ->
                            val isCorrect = option == correctAnswer
                            val isSelected = option == selectedAnswer
                            val (buttonColor, contentColor, borderColor) = when {
                                selectedAnswer != null && isCorrect -> Triple(Color(0xFFD1F0DB), Color(0xFF00391C), Color(0xFF00391C).copy(alpha = 0.2f)) // Sage Green
                                selectedAnswer != null && isSelected && !isCorrect -> Triple(Color(0xFFFFDAD6), Color(0xFFB3261E), Color(0xFFB3261E).copy(alpha = 0.2f)) // Warm Rose
                                else -> Triple(Color.White, Color(0xFF1B1B1F), Color(0xFFC7C6CA))
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = buttonColor),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .clickable(enabled = gameActive && !isAnsweringLocked) {
                                        submitAnswer(option)
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .testTag("math_choice_$option")
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Game mechanics summary text
            Text(
                text = "Correct answer: +10 pts | Wrong: -5 pts",
                fontSize = 12.sp,
                color = Color(0xFF44464F),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
