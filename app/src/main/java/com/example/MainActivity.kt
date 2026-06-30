package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.games.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = false, dynamicColor = false) {
        val viewModel: GamesViewModel = viewModel()
        val currentGame by viewModel.currentGame.collectAsState()

        when (currentGame) {
          null -> {
            DashboardScreen(
              viewModel = viewModel,
              onGameSelected = { game -> viewModel.selectGame(game) }
            )
          }
          GameType.TIC_TAC_TOE -> {
            TicTacToeGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.NUMBER_GUESSING -> {
            NumberGuessingGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.ROCK_PAPER_SCISSORS -> {
            RockPaperScissorsGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.MEMORY_MATCH -> {
            MemoryMatchGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.SNAKE -> {
            SnakeGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.WHACK_A_MOLE -> {
            WhackAMoleGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.MATH_QUIZ -> {
            MathQuizGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.WORD_SCRAMBLE -> {
            WordScrambleGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.TAP_SPEED -> {
            TapSpeedGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
          GameType.COIN_DICE -> {
            CoinDiceGame(viewModel = viewModel, onBack = { viewModel.selectGame(null) })
          }
        }
      }
    }
  }
}
