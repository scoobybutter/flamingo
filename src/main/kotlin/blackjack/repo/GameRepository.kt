package blackjack.repo

import blackjack.domain.Game

object GameRepository {
    private val gameIdGameRepository = mutableMapOf<String, Game>()

    fun addOrUpdateGame(gameId: String, game: Game): Game {
        gameIdGameRepository[gameId] = game
        return game
    }

    fun getGame(gameId: String) = gameIdGameRepository[gameId] ?: error("Game $gameId not found")
}