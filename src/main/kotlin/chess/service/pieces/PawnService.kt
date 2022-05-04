package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

class PawnService : PieceService() {
    private val SPOT_INCREMENTS_MOVE = listOf(Pair(0, 1))
    private val SPOT_INCREMENTS_MOVE_FIRST = listOf(Pair(0, 2))
    private val SPOT_INCREMENTS_TAKE = listOf(Pair(-1, 1), Pair(1, 1))

    override fun getPieceType(): PieceType {
        return PieceType.PAWN
    }

    override fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean {
        if (validateStopIncrements(
                startBox,
                destBox,
                SPOT_INCREMENTS_MOVE,
                includeThreat = false
            )
        ) return true
        if (startBox.y == 1 || startBox.y == board.size - 2) {
            if (validateStopIncrements(
                    startBox,
                    destBox,
                    SPOT_INCREMENTS_MOVE_FIRST,
                    includeThreat = false
                )
            ) return true
        }
        if (validateStopIncrements(
                startBox,
                destBox,
                SPOT_INCREMENTS_TAKE,
                includeThreat = true
            )
        ) return true
        return false
    }

    override fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>> {
        return getThreateningStopIncrements(board, startBox, SPOT_INCREMENTS_TAKE)
    }
}