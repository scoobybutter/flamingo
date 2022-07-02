package blackjack.service

import blackjack.domain.Card

object CardService {
    fun getMinValue(card: Card): Int {
        if (card.faceValue in 1..9) return card.faceValue
        return 10
    }

    fun getMaxValue(card: Card): Int {
        if (card.faceValue == 1) return 11
        if (card.faceValue in 2..9) return card.faceValue
        return 10
    }

    fun dealCard(card: Card, isFaceUp: Boolean = true): Card {
        return card.copy(isFaceUp = isFaceUp, isDealt = true)
    }
}