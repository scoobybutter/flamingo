package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

class BishopService : PieceService() {
    private val BEAM_INCREMENTS = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))
    override fun getPieceType(): PieceType {
        return PieceType.BISHOP
    }

    override fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean {
        return validateBeamIncrements(board, startBox, destBox, BEAM_INCREMENTS)
    }

    override fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>> {
        return getThreateningBeamIncrements(board, startBox, BEAM_INCREMENTS)
    }
}
