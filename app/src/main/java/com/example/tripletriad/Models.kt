package com.example.tripletriad

// Enum per saber de qui és la carta
enum class Player { NONE, PLAYER_1, OPPONENT }

// El model de dades d'una Carta de Triple Triad
data class Card(
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int,
    var owner: Player = Player.NONE,
    val name: String = "",
    val element: CardElement = CardElement.NONE,
    val rarity: CardRarity = CardRarity.COMMON
)

enum class CardElement {
    NONE, FIRE, ICE, THUNDER, EARTH, WIND, WATER, HOLY, POISON
}

enum class CardRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}

// Factoría per generar cartes amb noms temàtics
object CardFactory {
    private val cardNames = listOf(
        "Ifrit", "Shiva", "Ramuh", "Titan", "Bahamut",
        "Leviathan", "Diablos", "Carbuncle", "Tonberry", "Cactuar",
        "Squall", "Rinoa", "Zell", "Selphie", "Irvine",
        "Seifer", "Quistis", "Edea", "Laguna", "Kiros",
        "Gerogero", "Blobra", "Bite Bug", "Fastitocalon", "Gesper"
    )

    private val elements = CardElement.entries.toTypedArray()

    fun generateRandom(owner: Player = Player.NONE, rarity: CardRarity = CardRarity.COMMON): Card {
        val maxVal = when (rarity) {
            CardRarity.COMMON -> 5
            CardRarity.UNCOMMON -> 6
            CardRarity.RARE -> 7
            CardRarity.EPIC -> 8
            CardRarity.LEGENDARY -> 9
        }
        val minVal = maxOf(1, maxVal - 3)

        return Card(
            top    = (minVal..maxVal).random(),
            bottom = (minVal..maxVal).random(),
            left   = (minVal..maxVal).random(),
            right  = (minVal..maxVal).random(),
            owner  = owner,
            name   = cardNames.random(),
            element = elements.random(),
            rarity  = rarity
        )
    }

    fun generateHand(owner: Player, count: Int = 5): List<Card> {
        return List(count) { generateRandom(owner) }
    }
}