package chess.service.pieces

import chess.domain.Board
import chess.domain.Box
import chess.domain.PieceType

abstract class PieceService {
    abstract fun getPieceType(): PieceType
    abstract fun isMoveValid(board: Board, startBox: Box, destBox: Box): Boolean
    abstract fun getThreateningLocations(board: Board, startBox: Box): List<Pair<Int, Int>>

    protected fun validateBeamIncrements(
        board: Board,
        startBox: Box,
        destBox: Box,
        increments: List<Pair<Int, Int>>
    ): Boolean {
        increments.forEach { pair ->
            var newX = startBox.x + pair.first
            var newY = startBox.y + pair.second
            while (newX >= 0 && newX < board.size && newY >= 0 && newY < board.size) {
                if (newX == destBox.x && newY == destBox.y) {
                    if (destBox.piece == null || destBox.piece!!.color != startBox.piece!!.color) {
                        return true
                    }
                    return false
                }
                if (board.boxes[newX][newY].piece != null) break
                newX += pair.first
                newY += pair.second
            }
        }
        return false
    }

    protected fun validateStopIncrements(
        startBox: Box,
        destBox: Box,
        increments: List<Pair<Int, Int>>,
        includeThreat: Boolean = true
    ): Boolean {
        increments.forEach { pair ->
            if (startBox.x + pair.first == destBox.x && startBox.y + pair.second == destBox.y) {
                if (destBox.piece == null || (includeThreat && destBox.piece!!.color != startBox.piece!!.color)) {
                    return true
                }
                return false
            }
        }
        return false
    }

    protected fun getThreateningBeamIncrements(
        board: Board,
        startBox: Box,
        increments: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        val result = emptyList<Pair<Int, Int>>().toMutableList()
        increments.forEach { pair ->
            var newX = startBox.x + pair.first
            var newY = startBox.y + pair.second
            while (newX >= 0 && newX < board.size && newY >= 0 && newY < board.size) {
                if (board.boxes[newX][newY].piece != null) break
                result.add(Pair(newX, newY))
                newX += pair.first
                newY += pair.second
            }
        }
        return result
    }

    protected fun getThreateningStopIncrements(
        board: Board,
        startBox: Box,
        increments: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        val result = emptyList<Pair<Int, Int>>().toMutableList()
        increments.forEach { pair ->
            val newX = startBox.x + pair.first
            val newY = startBox.y + pair.second
            if (newX < 0 || newX >= board.size || newY < 0 || newY >= board.size) return@forEach
            if (board.boxes[newX][newY].piece == null) result.add(Pair(newX, newY))
        }
        return result
    }
}