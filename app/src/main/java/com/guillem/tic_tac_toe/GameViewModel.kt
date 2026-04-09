package com.guillem.tic_tac_toe

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    var board by mutableStateOf(Array(9) { "" })
    var isPlayer1Turn by mutableStateOf(true)
    var winner by mutableStateOf<String?>(null)
    var isDraw by mutableStateOf(false)

    fun onDrop(index: Int, p1: String, p2: String, isVsCpu: Boolean) {
        if (board[index] == "" && winner == null) {
            val currentSymbol = if (isPlayer1Turn) "X" else "O"
            val newBoard = board.copyOf()
            newBoard[index] = currentSymbol
            board = newBoard

            checkWinner(p1, p2)

            if (winner == null && !board.contains("")) {
                isDraw = true
            } else if (winner == null) {
                isPlayer1Turn = !isPlayer1Turn
                if (isVsCpu && !isPlayer1Turn) {
                    cpuMove(p1, p2)
                }
            }
        }
    }

    private fun cpuMove(p1: String, p2: String) {
        viewModelScope.launch {
            delay(600)
            val emptyIndices = board.indices.filter { board[it] == "" }
            if (emptyIndices.isNotEmpty() && winner == null) {
                onDrop(emptyIndices.random(), p1, p2, false)
            }
        }
    }

    private fun checkWinner(p1: String, p2: String) {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (p in winPatterns) {
            if (board[p[0]] != "" && board[p[0]] == board[p[1]] && board[p[1]] == board[p[2]]) {
                winner = if (board[p[0]] == "X") p1 else p2
            }
        }
    }

    fun resetGame() {
        board = Array(9) { "" }
        isPlayer1Turn = true
        winner = null
        isDraw = false
    }
}