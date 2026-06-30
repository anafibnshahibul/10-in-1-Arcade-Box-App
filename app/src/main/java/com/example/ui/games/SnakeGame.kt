package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

enum class SnakeDirection { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    val gridSize = 20

    var snake by remember { mutableStateOf(listOf(Pair(10, 10), Pair(10, 11), Pair(10, 12))) }
    var direction by remember { mutableStateOf(SnakeDirection.UP) }
    var food by remember { mutableStateOf(Pair(5, 5)) }
    var score by remember { mutableStateOf(0) }
    var gameActive by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    // Generate random food position that doesn't overlap the snake
    fun generateFood(): Pair<Int, Int> {
        var newFood: Pair<Int, Int>
        do {
            newFood = Pair((0 until gridSize).random(), (0 until gridSize).random())
        } while (snake.contains(newFood))
        return newFood
    }

    fun restartGame() {
        snake = listOf(Pair(10, 10), Pair(10, 11), Pair(10, 12))
        direction = SnakeDirection.UP
        food = Pair(5, 5)
        score = 0
        isGameOver = false
        gameActive = true
    }

    // Main Game Loop
    LaunchedEffect(key1 = gameActive) {
        while (gameActive) {
            delay(180) // speed of snake
            val head = snake.first()
            val nextHead = when (direction) {
                SnakeDirection.UP -> Pair(head.first, head.second - 1)
                SnakeDirection.DOWN -> Pair(head.first, head.second + 1)
                SnakeDirection.LEFT -> Pair(head.first - 1, head.second)
                SnakeDirection.RIGHT -> Pair(head.first + 1, head.second)
            }

            // Check Collision with boundary
            if (nextHead.first < 0 || nextHead.first >= gridSize || nextHead.second < 0 || nextHead.second >= gridSize) {
                gameActive = false
                isGameOver = true
                viewModel.saveSnakeHighScore(score)
                break
            }

            // Check Collision with self
            if (snake.contains(nextHead)) {
                gameActive = false
                isGameOver = true
                viewModel.saveSnakeHighScore(score)
                break
            }

            // Move Snake
            val newSnake = mutableListOf(nextHead)
            if (nextHead == food) {
                // Eat food, grow, update score, spawn new food
                newSnake.addAll(snake)
                score += 10
                food = generateFood()
            } else {
                newSnake.addAll(snake.dropLast(1))
            }
            snake = newSnake
        }
    }

    val colors = minimalistThemeMap[GameType.SNAKE]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("snake_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Retro Snake", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
                        text = if (isGameOver) "GAME OVER 💀" else if (gameActive) "PLAYING" else "PAUSED",
                        fontWeight = FontWeight.Bold,
                        color = if (isGameOver) Color(0xFFB3261E) else colors.iconBg,
                        fontSize = 14.sp
                    )
                }
            }

            // Game Board
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Game Board Cells
                Column {
                    for (row in 0 until gridSize) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until gridSize) {
                                val currentCoord = Pair(col, row)
                                val isSnakeBody = snake.contains(currentCoord)
                                val isSnakeHead = snake.firstOrNull() == currentCoord
                                val isFood = food == currentCoord

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(1.dp)
                                        .background(
                                            color = when {
                                                isSnakeHead -> colors.iconBg
                                                isSnakeBody -> colors.iconBg.copy(alpha = 0.5f)
                                                isFood -> Color(0xFFB3261E) // Food is elegant soft red
                                                else -> Color.Transparent
                                            },
                                            shape = if (isFood || isSnakeHead) CircleShape else RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                // Play / Game Over Overlay
                if (!gameActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isGameOver) "GAME OVER" else "RETRO SNAKE",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGameOver) Color(0xFFB3261E) else colors.iconBg
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isGameOver) "Final Score: $score" else "Use the D-Pad below to steer",
                                fontSize = 14.sp,
                                color = Color(0xFF44464F)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { restartGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.testTag("snake_start")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("PLAY NOW", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Neon retro D-Pad Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // UP
                IconButton(
                    onClick = { if (direction != SnakeDirection.DOWN) direction = SnakeDirection.UP },
                    enabled = gameActive,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, CircleShape)
                        .border(
                            width = if (direction == SnakeDirection.UP) 2.dp else 1.dp,
                            color = if (direction == SnakeDirection.UP) colors.iconBg else Color(0xFFC7C6CA),
                            shape = CircleShape
                        )
                        .testTag("snake_up")
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Up",
                        tint = if (direction == SnakeDirection.UP) colors.iconBg else Color(0xFF44464F),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    // LEFT
                    IconButton(
                        onClick = { if (direction != SnakeDirection.RIGHT) direction = SnakeDirection.LEFT },
                        enabled = gameActive,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .border(
                                width = if (direction == SnakeDirection.LEFT) 2.dp else 1.dp,
                                color = if (direction == SnakeDirection.LEFT) colors.iconBg else Color(0xFFC7C6CA),
                                shape = CircleShape
                            )
                            .testTag("snake_left")
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Left",
                            tint = if (direction == SnakeDirection.LEFT) colors.iconBg else Color(0xFF44464F),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // RIGHT
                    IconButton(
                        onClick = { if (direction != SnakeDirection.LEFT) direction = SnakeDirection.RIGHT },
                        enabled = gameActive,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .border(
                                width = if (direction == SnakeDirection.RIGHT) 2.dp else 1.dp,
                                color = if (direction == SnakeDirection.RIGHT) colors.iconBg else Color(0xFFC7C6CA),
                                shape = CircleShape
                            )
                            .testTag("snake_right")
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Right",
                            tint = if (direction == SnakeDirection.RIGHT) colors.iconBg else Color(0xFF44464F),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // DOWN
                IconButton(
                    onClick = { if (direction != SnakeDirection.UP) direction = SnakeDirection.DOWN },
                    enabled = gameActive,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, CircleShape)
                        .border(
                            width = if (direction == SnakeDirection.DOWN) 2.dp else 1.dp,
                            color = if (direction == SnakeDirection.DOWN) colors.iconBg else Color(0xFFC7C6CA),
                            shape = CircleShape
                        )
                        .testTag("snake_down")
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Down",
                        tint = if (direction == SnakeDirection.DOWN) colors.iconBg else Color(0xFF44464F),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
