package chess.repo

import chess.domain.Game

class GameRepository {
    private val gameIdGameMap = emptyMap<Long, Game>().toMutableMap()

    fun addOrUpdateGame(id: Long, game: Game): Game {
        gameIdGameMap[id] = game
        return game
    }

    fun getGame(id: Long) = gameIdGameMap[id]
}