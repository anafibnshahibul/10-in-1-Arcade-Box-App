package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
    val id: Int,
    val symbol: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryMatchGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    val iconsList = listOf("🍎", "🍌", "🍒", "🍇", "🍉", "🍓", "🍍", "🥑")
    val coroutineScope = rememberCoroutineScope()

    var cards by remember { mutableStateOf(generateShuffledCards(iconsList)) }
    var firstFlippedIndex by remember { mutableStateOf<Int?>(null) }
    var secondFlippedIndex by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableStateOf(0) }
    var matchesFound by remember { mutableStateOf(0) }
    var showWinAlert by remember { mutableStateOf(false) }

    fun playCard(index: Int) {
        if (firstFlippedIndex != null && secondFlippedIndex != null) return // wait for comparison
        if (cards[index].isFaceUp || cards[index].isMatched) return

        cards = cards.toMutableList().apply {
            this[index] = this[index].copy(isFaceUp = true)
        }

        if (firstFlippedIndex == null) {
            firstFlippedIndex = index
        } else {
            secondFlippedIndex = index
            moves++

            val fIdx = firstFlippedIndex!!
            val sIdx = secondFlippedIndex!!

            if (cards[fIdx].symbol == cards[sIdx].symbol) {
                // Match found!
                coroutineScope.launch {
                    delay(300)
                    cards = cards.toMutableList().apply {
                        this[fIdx] = this[fIdx].copy(isMatched = true)
                        this[sIdx] = this[sIdx].copy(isMatched = true)
                    }
                    matchesFound++
                    firstFlippedIndex = null
                    secondFlippedIndex = null

                    if (matchesFound == iconsList.size) {
                        showWinAlert = true
                        viewModel.saveMemoryResult(moves)
                    }
                }
            } else {
                // No match, flip back
                coroutineScope.launch {
                    delay(1000)
                    cards = cards.toMutableList().apply {
                        this[fIdx] = this[fIdx].copy(isFaceUp = false)
                        this[sIdx] = this[sIdx].copy(isFaceUp = false)
                    }
                    firstFlippedIndex = null
                    secondFlippedIndex = null
                }
            }
        }
    }

    fun resetGame() {
        cards = generateShuffledCards(iconsList)
        firstFlippedIndex = null
        secondFlippedIndex = null
        moves = 0
        matchesFound = 0
        showWinAlert = false
    }

    val colors = minimalistThemeMap[GameType.MEMORY_MATCH]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("memory_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Memory Match", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
                        text = "Moves: $moves",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1F)
                    )
                    Text(
                        text = "Matches: $matchesFound / ${iconsList.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.iconBg
                    )
                }
            }

            // Cards Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(cards) { index, card ->
                        val isFaceUp = card.isFaceUp || card.isMatched

                        // Card Flip Animation using rotationY
                        val rotation by animateFloatAsState(
                            targetValue = if (isFaceUp) 180f else 0f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                        )

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    rotationY = rotation
                                    cameraDistance = 8 * density
                                }
                                .clickable { playCard(index) }
                                .background(
                                    color = if (isFaceUp) Color.White else colors.iconBg,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isFaceUp) Color(0xFFC7C6CA) else Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .testTag("memory_card_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation > 90f) {
                                // Content of card front side
                                Text(
                                    text = card.symbol,
                                    fontSize = 28.sp,
                                    modifier = Modifier.graphicsLayer {
                                        rotationY = 180f // invert text so it's readable
                                    }
                                )
                            } else {
                                // Design of card back side
                                Text(
                                    text = "❓",
                                    fontSize = 20.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Info & Reset
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (showWinAlert) {
                    Text(
                        text = "Perfect memory! 🎉 Completed in $moves moves.",
                        color = colors.iconBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                IconButton(
                    onClick = { resetGame() },
                    modifier = Modifier.testTag("memory_reset")
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Restart Game",
                        tint = Color(0xFF44464F),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

private fun generateShuffledCards(icons: List<String>): List<MemoryCard> {
    val doubleList = (icons + icons).shuffled()
    return doubleList.mapIndexed { index, symbol ->
        MemoryCard(id = index, symbol = symbol)
    }
}
