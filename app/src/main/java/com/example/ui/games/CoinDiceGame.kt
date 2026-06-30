package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CoinDiceGame(
    viewModel: GamesViewModel,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Coin, 1 = Dice
    val coroutineScope = rememberCoroutineScope()

    // Coin States
    var coinFace by remember { mutableStateOf("HEADS") }
    var isCoinFlipping by remember { mutableStateOf(false) }
    var coinRotation by remember { mutableStateOf(0f) }

    // Dice States
    var diceValue by remember { mutableStateOf(1) }
    var isDiceRolling by remember { mutableStateOf(false) }
    var diceRotation by remember { mutableStateOf(0f) }

    // Local roll logs for fun
    var rollsHistory by remember { mutableStateOf(listOf<String>()) }

    fun flipCoin() {
        if (isCoinFlipping) return
        isCoinFlipping = true
        viewModel.incrementRandomizerCount()

        coroutineScope.launch {
            // Animate spin effect
            var rot = 0f
            repeat(12) {
                rot += 180f
                coinRotation = rot
                coinFace = if (coinFace == "HEADS") "TAILS" else "HEADS"
                delay(60)
            }
            val finalFace = if (Math.random() > 0.5) "HEADS" else "TAILS"
            coinFace = finalFace
            isCoinFlipping = false
            rollsHistory = listOf("Flipped Coin: $finalFace") + rollsHistory.take(4)
        }
    }

    fun rollDice() {
        if (isDiceRolling) return
        isDiceRolling = true
        viewModel.incrementRandomizerCount()

        coroutineScope.launch {
            var rot = 0f
            repeat(10) {
                rot += 36f
                diceRotation = rot
                diceValue = (1..6).random()
                delay(80)
            }
            val finalValue = (1..6).random()
            diceValue = finalValue
            isDiceRolling = false
            rollsHistory = listOf("Rolled Dice: $finalValue") + rollsHistory.take(4)
        }
    }

    val colors = minimalistThemeMap[GameType.COIN_DICE]!!

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("coindice_back")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B1B1F))
                }
                Spacer(Modifier.width(8.dp))
                Text("Coin & Dice Roller", fontWeight = FontWeight.Bold, color = Color(0xFF1B1B1F), fontSize = 20.sp)
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
            // Tab Selectors (Styled Custom M3 Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tab Coin
                val isCoinTabSelected = activeTab == 0
                val coinTabBg = if (isCoinTabSelected) colors.cardBg else Color(0xFFF3F0F5)
                val coinTabBorder = if (isCoinTabSelected) colors.iconBg.copy(alpha = 0.2f) else Color(0xFFC7C6CA)
                val coinTabContentColor = if (isCoinTabSelected) colors.iconBg else Color(0xFF44464F)

                Card(
                    colors = CardDefaults.cardColors(containerColor = coinTabBg),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 0 }
                        .border(1.dp, coinTabBorder, RoundedCornerShape(28.dp))
                        .testTag("coindice_tab_coin")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.MonetizationOn,
                            contentDescription = "Coin Flip Mode",
                            tint = coinTabContentColor
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Coin Flip",
                            fontWeight = FontWeight.Bold,
                            color = coinTabContentColor
                        )
                    }
                }

                // Tab Dice
                val isDiceTabSelected = activeTab == 1
                val diceTabBg = if (isDiceTabSelected) colors.cardBg else Color(0xFFF3F0F5)
                val diceTabBorder = if (isDiceTabSelected) colors.iconBg.copy(alpha = 0.2f) else Color(0xFFC7C6CA)
                val diceTabContentColor = if (isDiceTabSelected) colors.iconBg else Color(0xFF44464F)

                Card(
                    colors = CardDefaults.cardColors(containerColor = diceTabBg),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 1 }
                        .border(1.dp, diceTabBorder, RoundedCornerShape(28.dp))
                        .testTag("coindice_tab_dice")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Casino,
                            contentDescription = "Dice Roller Mode",
                            tint = diceTabContentColor
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Roll Dice",
                            fontWeight = FontWeight.Bold,
                            color = diceTabContentColor
                        )
                    }
                }
            }

            // Central Randomizer Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFFF3F0F5), RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (activeTab == 0) {
                    // COIN DISPLAY
                    val animatedCoinRotation by animateFloatAsState(
                        targetValue = coinRotation,
                        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            rotationY = animatedCoinRotation
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(Color(0xFFFFF1AC), CircleShape) // Light gold Accent Coin
                                .border(1.dp, Color(0xFFE5C158), CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFFFAEC), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (coinFace == "HEADS") "👑" else "🦅",
                                    fontSize = 48.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = coinFace,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1F)
                        )
                    }
                } else {
                    // DICE DISPLAY
                    val animatedDiceRotation by animateFloatAsState(
                        targetValue = diceRotation,
                        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.rotate(animatedDiceRotation)
                    ) {
                        // Drawing realistic Dice Dot Grid
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.White, RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0xFFC7C6CA), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DiceDots(value = diceValue, dotColor = colors.iconBg)
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "VALUE: $diceValue",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1F)
                        )
                    }
                }
            }

            // Dynamic Action Button
            Button(
                onClick = { if (activeTab == 0) flipCoin() else rollDice() },
                enabled = if (activeTab == 0) !isCoinFlipping else !isDiceRolling,
                colors = ButtonDefaults.buttonColors(containerColor = colors.iconBg, contentColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(52.dp)
                    .testTag("coindice_action")
            ) {
                Text(
                    text = if (activeTab == 0) "FLIP COIN" else "ROLL DICE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Local History logs
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Recent Outcomes Log",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF44464F),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    if (rollsHistory.isEmpty()) {
                        Text(
                            text = "No runs made yet. Flip or Roll!",
                            fontSize = 12.sp,
                            color = Color(0xFF74777F)
                        )
                    } else {
                        rollsHistory.forEach { log ->
                            Text(
                                text = "• $log",
                                fontSize = 12.sp,
                                color = Color(0xFF44464F),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Composables to draw dice dot configurations
@Composable
fun DiceDots(value: Int, dotColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val r = 8.dp.toPx()

        val centers = when (value) {
            1 -> listOf(Offset(w / 2, h / 2))
            2 -> listOf(Offset(w / 4, h / 4), Offset(3 * w / 4, 3 * h / 4))
            3 -> listOf(Offset(w / 4, h / 4), Offset(w / 2, h / 2), Offset(3 * w / 4, 3 * h / 4))
            4 -> listOf(
                Offset(w / 4, h / 4), Offset(3 * w / 4, h / 4),
                Offset(w / 4, 3 * h / 4), Offset(3 * w / 4, 3 * h / 4)
            )
            5 -> listOf(
                Offset(w / 4, h / 4), Offset(3 * w / 4, h / 4),
                Offset(w / 2, h / 2),
                Offset(w / 4, 3 * h / 4), Offset(3 * w / 4, 3 * h / 4)
            )
            else -> listOf(
                Offset(w / 4, h / 4), Offset(3 * w / 4, h / 4),
                Offset(w / 4, h / 2), Offset(3 * w / 4, h / 2),
                Offset(w / 4, 3 * h / 4), Offset(3 * w / 4, 3 * h / 4)
            )
        }

        centers.forEach { center ->
            drawCircle(color = dotColor, radius = r, center = center)
        }
    }
}
