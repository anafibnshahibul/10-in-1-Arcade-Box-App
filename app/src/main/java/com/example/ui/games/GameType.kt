package com.example.ui.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class GameType(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val difficulty: String
) {
    TIC_TAC_TOE(
        title = "Tic Tac Toe",
        description = "Classic 3x3 grid matching against a smart AI opponent.",
        icon = Icons.Filled.Close,
        accentColor = Color(0xFFFF2E93), // Retro Neon Pink
        difficulty = "Easy to Hard"
    ),
    NUMBER_GUESSING(
        title = "Number Guessing",
        description = "Find the secret number with \"Too High / Too Low\" hints.",
        icon = Icons.Filled.QuestionMark,
        accentColor = Color(0xFF00F0FF), // Cyber Cyan
        difficulty = "Easy"
    ),
    ROCK_PAPER_SCISSORS(
        title = "Rock Paper Scissors",
        description = "Test your wits and luck in a quick match against the CPU.",
        icon = Icons.Filled.FrontHand,
        accentColor = Color(0xFFFF9F00), // Tangerine Orange
        difficulty = "Normal"
    ),
    MEMORY_MATCH(
        title = "Memory Match",
        description = "Flip and match pairs of colorful cards on a 4x4 grid.",
        icon = Icons.Filled.GridView,
        accentColor = Color(0xFFB026FF), // Neon Purple
        difficulty = "Normal"
    ),
    SNAKE(
        title = "Retro Snake",
        description = "Steer the snake, eat neon pellets, and don't bite your own tail!",
        icon = Icons.Filled.TrendingFlat,
        accentColor = Color(0xFF39FF14), // Lime Green
        difficulty = "Challenging"
    ),
    WHACK_A_MOLE(
        title = "Whack-A-Mole",
        description = "Tap fast-appearing moles on the grid before time runs out!",
        icon = Icons.Filled.FlashOn,
        accentColor = Color(0xFFFF073A), // Neon Red
        difficulty = "Challenging"
    ),
    MATH_QUIZ(
        title = "Quick Math Quiz",
        description = "Solve as many simple math operations as you can in 30 seconds.",
        icon = Icons.Filled.Calculate,
        accentColor = Color(0xFFFFE600), // Cyber Yellow
        difficulty = "Normal"
    ),
    WORD_SCRAMBLE(
        title = "Word Scramble",
        description = "Unscramble mixed-up letters to guess the hidden words.",
        icon = Icons.Filled.Abc,
        accentColor = Color(0xFF1F51FF), // Neon Blue
        difficulty = "Normal"
    ),
    TAP_SPEED(
        title = "Tap Speed Test",
        description = "How fast can you tap the big red button in 10 seconds?",
        icon = Icons.Filled.TouchApp,
        accentColor = Color(0xFFE50914), // Crimson Red
        difficulty = "Easy"
    ),
    COIN_DICE(
        title = "Coin & Dice Randomizer",
        description = "Flip a neon coin or roll 3D-styled dice for instant decisions.",
        icon = Icons.Filled.Casino,
        accentColor = Color(0xFF00FF66), // Spring Green
        difficulty = "Casual"
    )
}
