package blackjack.service

import blackjack.domain.Card
import blackjack.domain.Hand

object HandService {

    fun possibleScores(hand: Hand): List<Int> {
        require(hand.cards.isNotEmpty()) {
            "cards aren't given to hand"
        }
        return calculateScores(hand.cards, 0, 0)
    }

    fun isBusted(hand: Hand): Boolean {
        return calculateScores(hand.cards, 0, 0).all { it > 21 }
    }

    fun isBlackJack(hand: Hand): Boolean {
        return hand.cards.size == 2 && calculateScores(hand.cards, 0, 0).contains(21)
    }

    fun dealCard(hand: Hand, card: Card): Hand {
        val updatedHand = hand.copy(cards = hand.cards + card)
        return updatedHand.copy(hasBusted = isBusted(updatedHand))
    }

    private fun calculateScores(cards: List<Card>, index: Int, sum: Int): List<Int> {
        if (index == cards.size) return listOf(sum)
        if (CardService.getMaxValue(cards[index]) == CardService.getMinValue(cards[index])) {
            return calculateScores(cards, index + 1, sum + CardService.getMinValue(cards[index]))
        }
        return calculateScores(
            cards,
            index + 1,
            sum + CardService.getMaxValue(cards[index])
        ) + calculateScores(
            cards,
            index + 1,
            sum + CardService.getMinValue(cards[index])
        )
    }
}