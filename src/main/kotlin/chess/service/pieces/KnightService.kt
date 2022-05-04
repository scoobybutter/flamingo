package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

class KnightService : PieceService() {

    private val SPOT_INCREMENTS = listOf(
        Pair(2, 1),
        Pair(2, -1),
        Pair(-2, 1),
        Pair(-2, -1),
        Pair(1, 2),
        Pair(1, -2),
        Pair(-1, 2),
        Pair(-1, -2)
    )

    override fun getPieceType(): PieceType {
        return PieceType.KNIGHT
    }

    override fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean {
        return validateStopIncrements(startBox, destBox, SPOT_INCREMENTS)
    }

    override fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>> {
        return getThreateningStopIncrements(board, startBox, SPOT_INCREMENTS)
    }
}