package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicTacToeGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameActive by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Your Turn (X)") }
    var winningLine by remember { mutableStateOf<List<Int>?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun checkWin(b: List<String>): List<Int>? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
            listOf(0, 4, 8), listOf(2, 4, 6)                  // diagonals
        )
        for (line in lines) {
            if (b[line[0]].isNotEmpty() && b[line[0]] == b[line[1]] && b[line[0]] == b[line[2]]) {
                return line
            }
        }
        return null
    }

    fun isBoardFull(b: List<String>): Boolean = b.all { it.isNotEmpty() }

    fun makeAiMove() {
        if (!gameActive) return
        statusMessage = "AI thinking..."
        coroutineScope.launch {
            delay(600) // fake thinking time for suspense
            val bestMove = getBestMove(board)
            if (bestMove != -1) {
                board = board.toMutableList().apply { this[bestMove] = "O" }
                val winLine = checkWin(board)
                if (winLine != null) {
                    winningLine = winLine
                    statusMessage = "AI Wins!"
                    gameActive = false
                    viewModel.saveTicTacToeResult(won = false, isDraw = false)
                } else if (isBoardFull(board)) {
                    statusMessage = "It's a Draw!"
                    gameActive = false
                    viewModel.saveTicTacToeResult(won = false, isDraw = true)
                } else {
                    isPlayerTurn = true
                    statusMessage = "Your Turn (X)"
                }
            }
        }
    }

    fun makePlayerMove(index: Int) {
        if (!gameActive || !isPlayerTurn || board[index].isNotEmpty()) return

        board = board.toMutableList().apply { this[index] = "X" }
        val winLine = checkWin(board)

        if (winLine != null) {
            winningLine = winLine
            statusMessage = "You Win! 🎉"
            gameActive = false
            viewModel.saveTicTacToeResult(won = true, isDraw = false)
        } else if (isBoardFull(board)) {
            statusMessage = "It's a Draw!"
            gameActive = false
            viewModel.saveTicTacToeResult(won = false, isDraw = true)
        } else {
            isPlayerTurn = false
            makeAiMove()
        }
    }

    fun resetGame() {
        board = List(9) { "" }
        isPlayerTurn = true
        gameActive = true
        winningLine = null
        statusMessage = "Your Turn (X)"
    }

    val colors = minimalistThemeMap[GameType.TIC_TAC_TOE]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("ttt_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Tic Tac Toe", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Status Card
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.iconBg.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = statusMessage,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (statusMessage.contains("Win") || statusMessage.contains("Draw")) colors.iconBg else Color(0xFF1B1B1F),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Grid Layout
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(28.dp))
                    .padding(16.dp)
            ) {
                // Lines Canvas for clean retro divider grids
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Draw grid dividers
                    val strokeWidth = 6f
                    val gridColor = Color(0xFFC7C6CA)

                    // Vertical lines
                    drawLine(gridColor, Offset(w / 3, 10f), Offset(w / 3, h - 10f), strokeWidth)
                    drawLine(gridColor, Offset(2 * w / 3, 10f), Offset(2 * w / 3, h - 10f), strokeWidth)

                    // Horizontal lines
                    drawLine(gridColor, Offset(10f, h / 3), Offset(w - 10f, h / 3), strokeWidth)
                    drawLine(gridColor, Offset(10f, 2 * h / 3), Offset(w - 10f, 2 * h / 3), strokeWidth)
                }

                // Interaction Grid
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..2) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0..2) {
                                val idx = row * 3 + col
                                val cellVal = board[idx]
                                val isWinningCell = winningLine?.contains(idx) == true

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(enabled = cellVal.isEmpty() && gameActive && isPlayerTurn) {
                                            makePlayerMove(idx)
                                        }
                                        .testTag("ttt_cell_$idx"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (cellVal.isNotEmpty()) {
                                        Text(
                                            text = cellVal,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isWinningCell -> Color(0xFFB3261E) // Warm highlight for win
                                                cellVal == "X" -> colors.iconBg
                                                else -> Color(0xFFFF2E93) // Accent pink-red
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { resetGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .testTag("ttt_reset")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Restart Match", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Simple heuristic AI Move Selector (Wins, Blocks, or takes Center, otherwise random)
private fun getBestMove(board: List<String>): Int {
    val lines = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )

    // 1. Can AI Win in 1 move?
    for (line in lines) {
        val values = line.map { board[it] }
        if (values.count { it == "O" } == 2 && values.count { it == "" } == 1) {
            return line[values.indexOf("")]
        }
    }

    // 2. Can AI Block Player from winning?
    for (line in lines) {
        val values = line.map { board[it] }
        if (values.count { it == "X" } == 2 && values.count { it == "" } == 1) {
            return line[values.indexOf("")]
        }
    }

    // 3. Take Center
    if (board[4] == "") return 4

    // 4. Take Corners
    val corners = listOf(0, 2, 6, 8).filter { board[it] == "" }
    if (corners.isNotEmpty()) return corners.random()

    // 5. Take whatever is left
    val emptyCells = board.mapIndexed { idx, value -> if (value == "") idx else -1 }.filter { it != -1 }
    return if (emptyCells.isNotEmpty()) emptyCells.random() else -1
}
