package blackjack.domain

data class Deck(
    val cards: List<Card>,
    val dealIndex: Int = 0
)
