package blackjack.domain

data class Game(
    val id: String,
    val hands: List<Hand>,
    val dealer: Hand,
    val deck: Deck,
    val handIndex: Int = 0
)