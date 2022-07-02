package blackjack.service

import blackjack.domain.Card
import blackjack.domain.Deck
import blackjack.domain.Suit

object DeckService {
    fun initialize(): Deck {
        return Deck(
            cards = (1..13).flatMap { faceValue -> Suit.values().map { Card(faceValue = faceValue, suit = it) } }
        )
    }

    fun shuffle(deck: Deck): Deck {
        return deck.copy(cards = deck.cards.shuffled())
    }
}