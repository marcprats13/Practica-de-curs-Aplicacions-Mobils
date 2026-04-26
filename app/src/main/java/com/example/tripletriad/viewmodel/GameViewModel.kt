package com.example.tripletriad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripletriad.model.Card
import com.example.tripletriad.utils.GameSettings
import com.example.tripletriad.model.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // Tablero
    val board = mutableStateListOf<Card?>().apply {
        repeat(9) { add(null) }
    }

    val gameStartTime = System.currentTimeMillis()

    // Game State
    var isPlayer1Turn by mutableStateOf(Random.Default.nextBoolean())

    init {
        // Si la máquina gana el sorteo inicial, empieza ella
        if (!isPlayer1Turn) {
            viewModelScope.launch {
                delay(1000) // Pausa de "pensamiento" antes de la primera carta
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

    var isBordersMode = false
    var isReverseMode = false
    // Objeto clásico de Android para contar hacia atrás
    private var timerJob: Job? = null


    // Player's Hand
    val playerHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(
                Card(
                    (1..9).random(),
                    (1..9).random(),
                    (1..9).random(),
                    (1..9).random(),
                    Player.PLAYER_1
                )
            )
        }
    }
    // Opponent's Hand
    val opponentHand = mutableStateListOf<Card>().apply {
        repeat(5) {
            add(
                Card(
                    (1..9).random(),
                    (1..9).random(),
                    (1..9).random(),
                    (1..9).random(),
                    Player.OPPONENT
                )
            )
        }
    }

    fun selectCard(card: Card) {
        if (isPlayer1Turn && !isGameOver) {
            selectedCard = card
        }
    }

    fun setGameRules(borders: Boolean, reverse: Boolean) {
        isBordersMode = borders
        isReverseMode = reverse
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
                    delay(1000) // Delay de 1 segundo para que el jugador vea la jugada

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
            var bestCard: Card? = null
            var bestIndex = -1
            var maxCaptures = -1

            // Algoritmo Greedy
            for (card in opponentHand) {
                for (index in emptyIndices) {
                    val simulatedCaptures = simulateCaptures(card, index)

                    // Si esta jugada captura más cartas que la anterior, entonces la guardamos
                    if (simulatedCaptures > maxCaptures) {
                        maxCaptures = simulatedCaptures
                        bestCard = card
                        bestIndex = index
                    } else if (simulatedCaptures == maxCaptures && Random.Default.nextBoolean()) {
                        // Si empata (ambas capturan 0), aplicamos un poco de azar
                        bestCard = card
                        bestIndex = index
                    }
                }
            }

            //Si algo falla, entonces tiramos de random por si acaso
            val finalIndex = if (bestIndex != -1) bestIndex else emptyIndices.random()
            val finalCard = bestCard ?: opponentHand.random()

            board[finalIndex] = finalCard.copy()
            opponentHand.remove(finalCard)

            checkCaptures(finalIndex)
            updateScores()
            checkGameOver()

            // Si el juego no ha terminado devolvemos el turno
            if (!isGameOver) {
                isPlayer1Turn = true
            }
        }
    }

    private fun checkCaptures(index: Int) {
        val currentCard = board[index] ?: return

        // Calculos para los modos frontera y normal
        val topNeighbor = if (isBordersMode && index < 3) index + 6 else index - 3
        val bottomNeighbor = if (isBordersMode && index > 5) index - 6 else index + 3
        val leftNeighbor = if (isBordersMode && index % 3 == 0) index + 2 else index - 1
        val rightNeighbor = if (isBordersMode && index % 3 == 2) index - 2 else index + 1

        val neighbors = listOf(
            Triple(topNeighbor, "TOP", "BOTTOM"),
            Triple(bottomNeighbor, "BOTTOM", "TOP"),
            Triple(leftNeighbor, "LEFT", "RIGHT"),
            Triple(rightNeighbor, "RIGHT", "LEFT")
        )

        for ((neighborIndex, position, _) in neighbors) {
            // Validar que el índice está dentro del tablero
            if (neighborIndex in 0..8) {

                // Si el Modo Fronteras está apagado, bloqueamos los saltos de fila
                if (!isBordersMode) {
                    if (position == "LEFT" && index % 3 == 0) continue
                    if (position == "RIGHT" && index % 3 == 2) continue
                }

                val neighborCard = board[neighborIndex]
                if (neighborCard != null && neighborCard.owner != currentCard.owner) {

                    // Función auxiliar para saber si ganamos la captura (Depende de modalidad normal o inversa)
                    fun wins(myStat: Int, oppStat: Int): Boolean {
                        return if (isReverseMode) myStat < oppStat else myStat > oppStat
                    }

                    val shouldCapture = when (position) {
                        "TOP" -> wins(currentCard.top, neighborCard.bottom)
                        "BOTTOM" -> wins(currentCard.bottom, neighborCard.top)
                        "LEFT" -> wins(currentCard.left, neighborCard.right)
                        "RIGHT" -> wins(currentCard.right, neighborCard.left)
                        else -> false
                    }

                    if (shouldCapture) {
                        board[neighborIndex] = neighborCard.copy(owner = currentCard.owner)
                    }
                }
            }
        }
    }

    // Función auxiliar para el algoritmo del greedy para la IA
    private fun simulateCaptures(card: Card, index: Int): Int {
        var captures = 0

        val topNeighbor = if (isBordersMode && index < 3) index + 6 else index - 3
        val bottomNeighbor = if (isBordersMode && index > 5) index - 6 else index + 3
        val leftNeighbor = if (isBordersMode && index % 3 == 0) index + 2 else index - 1
        val rightNeighbor = if (isBordersMode && index % 3 == 2) index - 2 else index + 1

        val neighbors = listOf(
            Triple(topNeighbor, "TOP", "BOTTOM"),
            Triple(bottomNeighbor, "BOTTOM", "TOP"),
            Triple(leftNeighbor, "LEFT", "RIGHT"),
            Triple(rightNeighbor, "RIGHT", "LEFT")
        )

        for ((neighborIndex, position, _) in neighbors) {
            if (neighborIndex in 0..8) {

                if (!isBordersMode) {
                    if (position == "LEFT" && index % 3 == 0) continue
                    if (position == "RIGHT" && index % 3 == 2) continue
                }

                val neighborCard = board[neighborIndex]
                if (neighborCard != null && neighborCard.owner == Player.PLAYER_1) {

                    fun wins(myStat: Int, oppStat: Int): Boolean {
                        return if (isReverseMode) myStat < oppStat else myStat > oppStat
                    }

                    val shouldCapture = when (position) {
                        "TOP" -> wins(card.top, neighborCard.bottom)
                        "BOTTOM" -> wins(card.bottom, neighborCard.top)
                        "LEFT" -> wins(card.left, neighborCard.right)
                        "RIGHT" -> wins(card.right, neighborCard.left)
                        else -> false
                    }

                    if (shouldCapture) captures++
                }
            }
        }
        return captures
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
        if (timerJob == null) { // Evitamos que se arranque dos veces al rotar la pantalla
            timeLeft = maxTimeSeconds

            timerJob = viewModelScope.launch { //
                while (timeLeft > 0 && !isGameOver) {
                    delay(GameSettings.AI_THINKING_DELAY) // Espera un segundo
                    timeLeft--

                    // "Control de si tiempo agotado"
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