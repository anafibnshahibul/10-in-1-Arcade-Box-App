package com.example.ui.games

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamesViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("arcade_box_scores", Context.MODE_PRIVATE)

    // Navigation State: null means Dashboard, otherwise the active game
    private val _currentGame = MutableStateFlow<GameType?>(null)
    val currentGame: StateFlow<GameType?> = _currentGame.asStateFlow()

    // Map of game name to high score string or value
    private val _highScores = MutableStateFlow<Map<String, String>>(emptyMap())
    val highScores: StateFlow<Map<String, String>> = _highScores.asStateFlow()

    init {
        loadHighScores()
    }

    fun selectGame(game: GameType?) {
        _currentGame.value = game
    }

    private fun loadHighScores() {
        val scores = mutableMapOf<String, String>()
        GameType.values().forEach { game ->
            val score = when (game) {
                GameType.TIC_TAC_TOE -> {
                    val w = sharedPrefs.getInt("ttt_wins", 0)
                    val l = sharedPrefs.getInt("ttt_losses", 0)
                    "Wins: $w | Losses: $l"
                }
                GameType.NUMBER_GUESSING -> {
                    val attempts = sharedPrefs.getInt("num_guess_best", 999)
                    if (attempts == 999) "No record" else "Best: $attempts attempts"
                }
                GameType.ROCK_PAPER_SCISSORS -> {
                    val streak = sharedPrefs.getInt("rps_streak", 0)
                    "Max Streak: $streak"
                }
                GameType.MEMORY_MATCH -> {
                    val moves = sharedPrefs.getInt("memory_best_moves", 999)
                    if (moves == 999) "No record" else "Best: $moves moves"
                }
                GameType.SNAKE -> {
                    val score = sharedPrefs.getInt("snake_high", 0)
                    "High Score: $score"
                }
                GameType.WHACK_A_MOLE -> {
                    val score = sharedPrefs.getInt("whack_high", 0)
                    "High Score: $score"
                }
                GameType.MATH_QUIZ -> {
                    val score = sharedPrefs.getInt("math_high", 0)
                    "High Score: $score"
                }
                GameType.WORD_SCRAMBLE -> {
                    val streak = sharedPrefs.getInt("word_streak", 0)
                    "Max Streak: $streak"
                }
                GameType.TAP_SPEED -> {
                    val score = sharedPrefs.getInt("tap_high", 0)
                    "Max: $score taps"
                }
                GameType.COIN_DICE -> {
                    val total = sharedPrefs.getInt("randomizer_total", 0)
                    "Runs: $total times"
                }
            }
            scores[game.name] = score
        }
        _highScores.value = scores
    }

    // Save functions for each game type
    fun saveTicTacToeResult(won: Boolean, isDraw: Boolean) {
        if (isDraw) return
        if (won) {
            val w = sharedPrefs.getInt("ttt_wins", 0) + 1
            sharedPrefs.edit().putInt("ttt_wins", w).apply()
        } else {
            val l = sharedPrefs.getInt("ttt_losses", 0) + 1
            sharedPrefs.edit().putInt("ttt_losses", l).apply()
        }
        loadHighScores()
    }

    fun saveNumberGuessingResult(attempts: Int) {
        val currentBest = sharedPrefs.getInt("num_guess_best", 999)
        if (attempts < currentBest) {
            sharedPrefs.edit().putInt("num_guess_best", attempts).apply()
            loadHighScores()
        }
    }

    fun updateRpsStreak(currentStreak: Int) {
        val maxStreak = sharedPrefs.getInt("rps_streak", 0)
        if (currentStreak > maxStreak) {
            sharedPrefs.edit().putInt("rps_streak", currentStreak).apply()
            loadHighScores()
        }
    }

    fun saveMemoryResult(moves: Int) {
        val currentBest = sharedPrefs.getInt("memory_best_moves", 999)
        if (moves < currentBest) {
            sharedPrefs.edit().putInt("memory_best_moves", moves).apply()
            loadHighScores()
        }
    }

    fun saveSnakeHighScore(score: Int) {
        val currentHigh = sharedPrefs.getInt("snake_high", 0)
        if (score > currentHigh) {
            sharedPrefs.edit().putInt("snake_high", score).apply()
            loadHighScores()
        }
    }

    fun saveWhackHighScore(score: Int) {
        val currentHigh = sharedPrefs.getInt("whack_high", 0)
        if (score > currentHigh) {
            sharedPrefs.edit().putInt("whack_high", score).apply()
            loadHighScores()
        }
    }

    fun saveMathHighScore(score: Int) {
        val currentHigh = sharedPrefs.getInt("math_high", 0)
        if (score > currentHigh) {
            sharedPrefs.edit().putInt("math_high", score).apply()
            loadHighScores()
        }
    }

    fun updateWordStreak(streak: Int) {
        val currentMax = sharedPrefs.getInt("word_streak", 0)
        if (streak > currentMax) {
            sharedPrefs.edit().putInt("word_streak", streak).apply()
            loadHighScores()
        }
    }

    fun saveTapHighScore(score: Int) {
        val currentHigh = sharedPrefs.getInt("tap_high", 0)
        if (score > currentHigh) {
            sharedPrefs.edit().putInt("tap_high", score).apply()
            loadHighScores()
        }
    }

    fun incrementRandomizerCount() {
        val count = sharedPrefs.getInt("randomizer_total", 0) + 1
        sharedPrefs.edit().putInt("randomizer_total", count).apply()
        loadHighScores()
    }

    fun resetAllScores() {
        sharedPrefs.edit().clear().apply()
        loadHighScores()
    }
}
