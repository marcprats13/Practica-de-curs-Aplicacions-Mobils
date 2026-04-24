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

    var isGameOver by mutableStateOf(false)
        private set

    // 3. LA MANO DEL JUGADOR (Player's Hand)
    // Para probar, generaremos 5 cartas aleatorias sencillas
    val playerHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(Card((1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(), Player.PLAYER_1))
        }
    }

    val opponentHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(Card((1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(), Player.OPPONENT))
        }
    }

    // Método para jugar una carta en el tablero
    fun playCard(boardIndex: Int, card: Card) {
        // Solo te dejamos jugar si la casilla está vacía y es tu turno
        if (board[boardIndex] == null && isPlayer1Turn) {
            board[boardIndex] = card.copy()
            playerHand.remove(card)

            checkCaptures(boardIndex)
            updateScores()
            checkGameOver()

            if (!isGameOver) {
                // Pasamos el turno y hacemos que la máquina juegue
                isPlayer1Turn = false
                playOpponentTurn()
            }
        }
    }

    private fun playOpponentTurn() {
        // Buscamos qué casillas quedan vacías en el tablero
        val emptyIndices = board.indices.filter { board[it] == null }

        if (emptyIndices.isNotEmpty() && opponentHand.isNotEmpty()) {
            // El oponente elige una casilla al azar y una carta al azar
            val randomIndex = emptyIndices.random()
            val randomCard = opponentHand.random()

            // Juega la carta
            board[randomIndex] = randomCard.copy()
            opponentHand.remove(randomCard)

            checkCaptures(randomIndex)
            updateScores()
            checkGameOver()

            // Si el juego no ha terminado, te devuelve el turno
            if (!isGameOver) {
                isPlayer1Turn = true
            }
        }
    }

    private fun checkCaptures(index: Int) {
        val currentCard = board[index] ?: return

        // Definimos los vecinos: arriba, abajo, izquierda, derecha
        // Para un tablero 3x3 (0-8):
        val neighbors = listOf(
            Triple(index - 3, "TOP", "BOTTOM"),    // Vecino de arriba
            Triple(index + 3, "BOTTOM", "TOP"),    // Vecino de abajo
            Triple(index - 1, "LEFT", "RIGHT"),    // Vecino de izquierda (ojo con bordes)
            Triple(index + 1, "RIGHT", "LEFT")     // Vecino de derecha (ojo con bordes)
        )

        for ((neighborIndex, position, opposite) in neighbors) {
            // Validar que el índice existe y no se sale de los bordes del 3x3
            if (neighborIndex in 0..8) {
                // Evitar saltos de fila en izquierda/derecha
                if (position == "LEFT" && index % 3 == 0) continue
                if (position == "RIGHT" && index % 3 == 2) continue

                val neighborCard = board[neighborIndex]
                if (neighborCard != null && neighborCard.owner != currentCard.owner) {

                    // Comparación de valores según la posición
                    val shouldCapture = when (position) {
                        "TOP" -> currentCard.top > neighborCard.bottom
                        "BOTTOM" -> currentCard.bottom > neighborCard.top
                        "LEFT" -> currentCard.left > neighborCard.right
                        "RIGHT" -> currentCard.right > neighborCard.left
                        else -> false
                    }

                    if (shouldCapture) {
                        // Cambiamos el dueño de la carta (Captura)
                        board[neighborIndex] = neighborCard.copy(owner = currentCard.owner)
                    }
                }
            }
        }
    }

    private fun updateScores() {
        var p1 = 0
        var opp = 0
        board.forEach { card ->
            when (card?.owner) {
                Player.PLAYER_1 -> p1++
                Player.OPPONENT -> opp++
                else -> {}
            }
        }
        // Sumamos las cartas que aún quedan en las manos
        playerScore = p1 + playerHand.size
        opponentScore = opp + opponentHand.size // (Simulación del rival)
    }
    fun checkGameOver() {
        // El juego termina si no quedan huecos en el tablero
        if (board.all { it != null }) {
            isGameOver = true
        }
    }
}