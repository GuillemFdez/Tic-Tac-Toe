package com.guillem.tic_tac_toe

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Classe que gestiona la lògica i l'estat del joc
class GameViewModel : ViewModel() {

    // Tauler de joc representat per una llista de nou cadenes de text (buides, "X" o "O")
    // S'utilitza mutableStateOf perquè Compose detecti els canvis i actualitzi la pantalla
    var board by mutableStateOf(Array(9) { "" })

    // Controla quin jugador té el torn (cert per al Jugador 1, fals per al Jugador 2/CPU)
    var isPlayer1Turn by mutableStateOf(true)

    // Guarda el nom del guanyador un cop finalitzada la partida
    var winner by mutableStateOf<String?>(null)

    // Indica si la partida ha acabat en empat
    var isDraw by mutableStateOf(false)

    // Funció que s'executa quan un jugador prem una casella del tauler
    fun onDrop(index: Int, p1: String, p2: String, isVsCpu: Boolean) {
        // Comprovem que la casella estigui buida i que no hi hagi ja un guanyador
        if (board[index] == "" && winner == null) {
            // Assignem el símbol corresponent segons el torn actual
            val currentSymbol = if (isPlayer1Turn) "X" else "O"
            val newBoard = board.copyOf()
            newBoard[index] = currentSymbol
            board = newBoard

            // Verifiquem si aquest moviment ha guanyat la partida
            checkWinner(p1, p2)

            // Si no hi ha guanyador, mirem si el tauler està ple (empat)
            if (winner == null && !board.contains("")) {
                isDraw = true
            } else if (winner == null) {
                // Si la partida continua, canviem el torn
                isPlayer1Turn = !isPlayer1Turn

                // Si estem en mode contra la màquina i és el torn de la CPU, l'activem
                if (isVsCpu && !isPlayer1Turn) {
                    cpuMove(p1, p2)
                }
            }
        }
    }

    // Lògica per al moviment automàtic de l'ordinador
    private fun cpuMove(p1: String, p2: String) {
        // Executem el moviment dins d'una corrutina per no bloquejar la interfície
        viewModelScope.launch {
            // Afegim un petit retard perquè el moviment no sigui instantani i sembli més natural
            delay(600)

            // Cerquem quines caselles estan lliures actualment
            val emptyIndices = board.indices.filter { board[it] == "" }

            // Si hi ha lloc i ningú ha guanyat encara, triem una posició a l'atzar
            if (emptyIndices.isNotEmpty() && winner == null) {
                onDrop(emptyIndices.random(), p1, p2, false)
            }
        }
    }

    // Comprova totes les combinacions possibles per guanyar
    private fun checkWinner(p1: String, p2: String) {
        // Definim les línies guanyadores (horitzontals, verticals i diagonals)
        // No és súper escalable però funciona :D
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Horitzontals
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Verticals
            listOf(0, 4, 8), listOf(2, 4, 6)                 // Diagonals
        )

        // Revisem cada combinació per veure si les tres caselles tenen el mateix símbol
        for (p in winPatterns) {
            if (board[p[0]] != "" && board[p[0]] == board[p[1]] && board[p[1]] == board[p[2]]) {
                // Assignem el nom del guanyador depenent del símbol que hi ha a la línia
                winner = if (board[p[0]] == "X") p1 else p2
            }
        }
    }

    // Reinicia tots els valors per poder començar una partida nova des de zero
    fun resetGame() {
        board = Array(9) { "" }
        isPlayer1Turn = true
        winner = null
        isDraw = false
    }
}