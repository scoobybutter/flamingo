package blackjack.domain

data class Card(
    val suit: Suit,
    val faceValue: Int,
    val isDealt: Boolean = false,
    val isFaceUp: Boolean = true
)

enum class Suit {
    CLUB,
    DIAMOND,
    HEART,
    SPADE
}
