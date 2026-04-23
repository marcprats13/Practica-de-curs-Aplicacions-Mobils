package com.example.tripletriad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // 1. EL TABLERO (The Board): Una lista de 9 posiciones (3x3).
    // Usamos 'null' para representar una casilla vacía.
    val board = mutableStateListOf<Card?>().apply {
        repeat(9) { add(null) }
    }

    // 2. ESTADO DEL JUEGO (Game State)
    var isPlayer1Turn by mutableStateOf(true) // true = tu turno, false = turno oponente
    var playerScore by mutableStateOf(5) // En Triple Triad empiezas con 5 cartas
    var opponentScore by mutableStateOf(5)

    // 3. LA MANO DEL JUGADOR (Player's Hand)
    // Para probar, generaremos 5 cartas aleatorias sencillas
    val playerHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(Card((1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(), Player.PLAYER_1))
        }
    }

    // Método para jugar una carta en el tablero
    fun playCard(boardIndex: Int, card: Card) {
        // Solo podemos poner la carta si la casilla está vacía
        if (board[boardIndex] == null) {
            board[boardIndex] = card
            playerHand.remove(card) // La quitamos de la mano

            // TODO: Aquí implementaremos la lógica de "Combate" (voltear cartas vecinas)

            // Cambiamos de turno
            isPlayer1Turn = !isPlayer1Turn
        }
    }
}