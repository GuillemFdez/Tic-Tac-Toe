package com.guillem.tic_tac_toe

import org.junit.Test
import kotlin.test.assertEquals

class LogicTest {

    // Funció simulada de lògica de joc (Pots posar-la al codi principal més endavant)
    fun checkWinner(board: List<String>): String? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Horitzontals
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Verticals
            listOf(0, 4, 8), listOf(2, 4, 6)             // Diagonals
        )
        for (p in winPatterns) {
            if (board[p[0]] != "" && board[p[0]] == board[p[1]] && board[p[0]] == board[p[2]]) {
                return board[p[0]]
            }
        }
        return null
    }

    @Test
    fun testWinnerX() {
        val board = listOf("X", "X", "X", "", "O", "", "", "O", "")
        assertEquals("O", checkWinner(board)) // Això fallarà perquè el guanyador és X
    }

    @Test
    fun testNoWinner() {
        val board = listOf("X", "O", "X", "", "", "", "", "", "")
        org.junit.Assert.assertNull(checkWinner(board))
    }
}