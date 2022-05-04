package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

class KingService : PieceService() {
    private val SPOT_INCREMENTS = listOf(
        Pair(1, -1),
        Pair(1, 0),
        Pair(1, 1),
        Pair(0, 1),
        Pair(-1, 1),
        Pair(-1, 0),
        Pair(-1, -1),
        Pair(0, -1)
    )

    override fun getPieceType(): PieceType {
        return PieceType.KING
    }


    override fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean {
        return validateStopIncrements(startBox, destBox, SPOT_INCREMENTS)
    }

    override fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>> {
        return getThreateningStopIncrements(board, startBox, SPOT_INCREMENTS)
    }
}