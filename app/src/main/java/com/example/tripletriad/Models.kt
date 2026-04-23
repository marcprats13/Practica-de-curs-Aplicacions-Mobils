package com.example.tripletriad

// Enum para saber de quién es la carta
enum class Player { NONE, PLAYER_1, OPPONENT }

// El modelo de datos de una Carta de Triple Triad
data class Card(
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int,
    var owner: Player = Player.NONE
)