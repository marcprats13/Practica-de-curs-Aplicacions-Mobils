package com.example.tripletriad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // Tablero
    val board = mutableStateListOf<Card?>().apply {
        repeat(9) { add(null) }
    }

    // Game State
    var isPlayer1Turn by mutableStateOf(Random.nextBoolean())

    init {
        // Si la máquina gana el sorteo inicial, empieza ella
        if (!isPlayer1Turn) {
            viewModelScope.launch {
                delay(1000) // Pausa de "pensamiento" antes de su primera carta
                playOpponentTurn()
            }
        }
    }
    var playerScore by mutableIntStateOf(5) // Empezamos con 5 cartas
    var opponentScore by mutableIntStateOf(5)

    var isGameOver by mutableStateOf(false)
        private set

    var selectedCard by mutableStateOf<Card?>(null)
        private set
    var timeLeft by mutableIntStateOf(25) // Por defecto 25, pero lo cambiaremos
        private set

    // Objeto clásico de Android para contar hacia atrás
    private var timerJob: Job? = null


    // Player's Hand
    val playerHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(Card((1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(), Player.PLAYER_1))
        }
    }
    // Opponent's Hand
    val opponentHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(Card((1..9).random(), (1..9).random(), (1..9).random(), (1..9).random(), Player.OPPONENT))
        }
    }

    fun selectCard(card: Card) {
        if (isPlayer1Turn && !isGameOver) {
            selectedCard = card
        }
    }

    // Método para jugar una carta en el tablero
    fun playCard(boardIndex: Int) {
        val cardToPlay = selectedCard

        if (timeLeft > 0 && cardToPlay != null && board[boardIndex] == null && isPlayer1Turn && !isGameOver) {
            board[boardIndex] = cardToPlay.copy()
            playerHand.remove(cardToPlay)
            selectedCard = null

            checkCaptures(boardIndex)
            updateScores()
            checkGameOver()

            if (!isGameOver) {
                // Pasamos el turno y hacemos que la máquina juegue
                isPlayer1Turn = false

                viewModelScope.launch {
                    delay(1000) // Espera 0.8 segundos para que el usuario vea su jugada

                    // Si el juego no ha terminado por tiempo en este medio segundo, la máquina juega
                    if (!isGameOver) {
                        playOpponentTurn()
                    }
                }
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
        playerScore = p1
        opponentScore = opp
    }

    fun startTimer(maxTimeSeconds: Int) {
        if (timerJob == null) { // Evita que se arranque dos veces al rotar la pantalla
            timeLeft = maxTimeSeconds

            timerJob = viewModelScope.launch { //
                while (timeLeft > 0 && !isGameOver) {
                    delay(1000L) // Espera un segundo
                    timeLeft--

                    // RÚBRICA: "Control de si tiempo agotado"
                    if (timeLeft <= 0) {
                        isGameOver = true
                    }
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }
    fun checkGameOver() {
        // El juego termina si no quedan huecos en el tablero
        if (board.all { it != null }) {
            isGameOver = true
            stopTimer()
        }
    }
}