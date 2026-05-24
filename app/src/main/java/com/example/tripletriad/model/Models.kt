package com.example.tripletriad.model

// Enum para saber de quien es la carta
enum class Player { NONE, PLAYER_1, OPPONENT }

enum class GameEndReason { NONE, BOARD_FULL, TIME_OUT }

// El model de datos de una Carta de Triple Triad
data class Card(
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int,
    var owner: Player = Player.NONE,
)


