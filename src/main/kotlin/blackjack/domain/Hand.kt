package blackjack.domain

data class Hand(
    val name: String,
    val cards: List<Card>,
    val bet: Double,
    val hasBusted: Boolean = false,
    val hasWon: Boolean = false,
    val wonAmount: Double = 0.0
)
