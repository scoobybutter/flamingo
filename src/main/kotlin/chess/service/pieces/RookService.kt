package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

class RookService : PieceService() {

    private val BEAM_INCREMENTS = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))

    override fun getPieceType(): PieceType {
        return PieceType.ROOK
    }

    override fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean {
        return validateBeamIncrements(board, startBox, destBox, BEAM_INCREMENTS)
    }

    override fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>> {
        return getThreateningBeamIncrements(board, startBox, BEAM_INCREMENTS)
    }
}