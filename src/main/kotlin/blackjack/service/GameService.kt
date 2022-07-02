package blackjack.service

import blackjack.domain.Game
import blackjack.domain.Hand
import blackjack.repo.GameRepository
import java.util.UUID

class GameService {
    fun createGame(players: List<String>, bet: Double): Game {
        val game = Game(
            id = UUID.randomUUID().toString(),
            hands = players.map {
                Hand(
                    name = it,
                    cards = emptyList(),
                    bet = bet
                )
            },
            dealer = Hand(
                name = "Dealer",
                cards = emptyList(),
                bet = bet
            ),
            deck = DeckService.shuffle(DeckService.initialize())
        )
        return GameRepository.addOrUpdateGame(game.id, game)
    }

    fun initialDeal(gameId: String) {
        var game = GameRepository.getGame(gameId)
        repeat(2 * game.hands.size) {
            game = dealCardToPlayer(game)
            game = game.copy(handIndex = (game.handIndex + 1) % game.hands.size)
        }
        repeat(2) {
            game = dealCardToDealer(game)
        }
        game = game.copy(hands = game.hands.map {
            if (HandService.isBlackJack(it)) it.copy(hasWon = true, wonAmount = 0.5 * it.bet)
            else it
        })
        game = game.copy(handIndex = game.hands.indexOfFirst { !it.hasWon })
        GameRepository.addOrUpdateGame(gameId, game)
    }

    fun handHit(gameId: String) {
        var game = GameRepository.getGame(gameId)
        require(!game.hands[game.handIndex].hasWon || !game.hands[game.handIndex].hasBusted) {
            "game over for the current hand"
        }
        game = dealCardToPlayer(game)
        GameRepository.addOrUpdateGame(gameId, game)
        if (game.hands[game.handIndex].hasBusted) {
            moveHandToNextPlayer(gameId)
        }
    }

    fun handStay(gameId: String) {
        moveHandToNextPlayer(gameId)
    }

    fun finishGame(gameId: String) {
        var game = GameRepository.getGame(gameId)
        var dealerScores = HandService.possibleScores(game.dealer)
        while (dealerScores.all { it < 17 }) {
            game = dealCardToDealer(game)
            dealerScores = HandService.possibleScores(game.dealer)
        }
        if (game.dealer.hasBusted) {
            game = game.copy(hands = game.hands.filter { !it.hasWon && !it.hasBusted }.map {
                it.copy(
                    hasWon = true,
                    wonAmount = it.bet
                )
            })
        } else {
            game = game.copy(hands = game.hands.filter { !it.hasWon && !it.hasBusted }.map { hand ->
                if (HandService.possibleScores(hand).any { it > (dealerScores.maxOrNull() ?: 0) }) hand.copy(
                    hasWon = true,
                    wonAmount = hand.bet
                )
                else hand
            })
        }
        GameRepository.addOrUpdateGame(gameId, game)
    }

    private fun moveHandToNextPlayer(gameId: String) {
        var game = GameRepository.getGame(gameId)
        do {
            game = game.copy(handIndex = (game.handIndex + 1))
            if (game.handIndex == game.hands.size) {
                break
            }
        } while (game.hands[game.handIndex].hasWon || game.hands[game.handIndex].hasBusted)
        GameRepository.addOrUpdateGame(gameId, game)
    }

    private fun dealCardToPlayer(game: Game): Game {
        val dealtCard = CardService.dealCard(game.deck.cards[game.deck.dealIndex])
        val dealtHand = HandService.dealCard(game.hands[game.handIndex], dealtCard)
        return game.copy(
            hands = game.hands.toMutableList().apply { this[game.handIndex] = dealtHand },
            deck = game.deck.copy(
                cards = game.deck.cards.toMutableList().apply { this[game.deck.dealIndex] = dealtCard },
                dealIndex = game.deck.dealIndex + 1
            )
        )
    }

    private fun dealCardToDealer(game: Game): Game {
        val dealtCard =
            CardService.dealCard(game.deck.cards[game.deck.dealIndex], isFaceUp = game.dealer.cards.isNotEmpty())
        val dealer = HandService.dealCard(game.dealer, dealtCard)
        return game.copy(
            dealer = dealer,
            deck = game.deck.copy(
                cards = game.deck.cards.toMutableList().apply { this[game.deck.dealIndex] = dealtCard },
                dealIndex = game.deck.dealIndex + 1
            )
        )
    }
}