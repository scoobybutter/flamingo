package chess.service

import chess.domain.*
import chess.repo.GameRepository

class GameService {
    private val gameRepository = GameRepository()
    private val boardService = BoardService()

    fun createGame(playerOne: String, playerTwo: String) {
        val game = Game(
            board = boardService.createBoard(),
            status = GameStatus.WHITE_MOVE,
            playerOne = Player(playerOne, Color.WHITE),
            playerTwo = Player(playerTwo, Color.BLACK)
        )
        gameRepository.addOrUpdateGame(id = game.id, game)
    }

    fun startGame(gameId: Long) {
        var game = gameRepository.getGame(gameId)!!
        while (game.status == GameStatus.WHITE_MOVE || game.status == GameStatus.BLACK_MOVE) {
            game = gameRepository.getGame(gameId)!!
            val move = getMoveFromUser(game)
            if (!boardService.isMoveValid(move, game.board, game.status)) {
                println("Invalid Move")
                continue
            }
            var updatedGame = game.copy(board = boardService.performMove(move, game.board))
            updatedGame = if (boardService.hasCurrentPlayerWon()) {
                updatedGame.copy(
                    status = if (game.status == GameStatus.WHITE_MOVE) GameStatus.WHITE_VICTORY
                    else GameStatus.BLACK_VICTORY
                )
            } else if (boardService.isDraw()) {
                updatedGame.copy(status = GameStatus.DRAW)
            } else {
                updatedGame.copy(
                    status = if (game.status == GameStatus.WHITE_MOVE) GameStatus.BLACK_MOVE
                    else GameStatus.WHITE_MOVE
                )
            }
            gameRepository.addOrUpdateGame(updatedGame.id, game)
        }
    }

    private fun getMoveFromUser(game: Game): Move {
        val moveString = readLine()!!
        val tokens = moveString.split(" ")
        return Move(
            gameId = game.id,
            startX = getCoordinatesFromLocationString(tokens[0]).first,
            startY = getCoordinatesFromLocationString(tokens[0]).second,
            endX = getCoordinatesFromLocationString(tokens[1]).first,
            endY = getCoordinatesFromLocationString(tokens[1]).second,
            player = if (game.status == GameStatus.WHITE_MOVE && game.playerOne.color == Color.WHITE) game.playerOne
            else game.playerTwo
        )
    }

    private fun getCoordinatesFromLocationString(location: String): Pair<Int, Int> {
        return Pair((location[0] - 'a').toInt(), location[1].toString().toInt() - 1)
    }
}