package chess.service

import chess.domain.*

class BoardService {
    private val pieceServiceFactory = PieceServiceFactory()

    fun createBoard(): Board {
        val boxes = (0..DEFAULT_SIZE).map { i ->
            (0..DEFAULT_SIZE).map { j ->
                Box(
                    i,
                    j,
                    INITIAL_COORDINATES_PIECE_TYPE_MAP[Pair(i, j)]?.let { Piece(Color.WHITE, it) }
                        ?: INITIAL_COORDINATES_PIECE_TYPE_MAP[Pair(
                            DEFAULT_SIZE - i - 1,
                            DEFAULT_SIZE - j - 1
                        )]?.let { Piece(Color.BLACK, it) }
                )
            }
        }
        return Board(size = DEFAULT_SIZE, boxes = boxes)
    }

    fun isMoveValid(move: Move, board: Board, gameStatus: GameStatus): Boolean {
        if (move.endX < 0 || move.endX >= board.size || move.endY < 0 || move.endY >= board.size) return false
        if (move.startX < 0 || move.startX >= board.size || move.startY < 0 || move.startY >= board.size) return false

        val sourceBox = board.boxes[move.startX][move.startY]
        val destBox = board.boxes[move.endX][move.endY]
        if (sourceBox.piece == null) return false

        if (
            (sourceBox.piece!!.color == Color.WHITE && gameStatus == GameStatus.BLACK_MOVE)
            || (sourceBox.piece!!.color == Color.BLACK && gameStatus == GameStatus.WHITE_MOVE)
        ) return false

        val currentPlayerColor = if (gameStatus == GameStatus.BLACK_MOVE) Color.BLACK else Color.WHITE
        val oppositePlayerColor = if (gameStatus == GameStatus.BLACK_MOVE) Color.WHITE else Color.BLACK
        if (pieceServiceFactory.getPieceService(sourceBox.piece!!.pieceType).isMoveValid(board, sourceBox, destBox)) {
            val updatedBoard = performMove(move, board)
            val currentKingBox = getCurrentKingBox(updatedBoard, currentPlayerColor)
            return updatedBoard.boxes
                .flatten()
                .filter { it.piece != null && it.piece!!.color == oppositePlayerColor }
                .all {
                    Pair(
                        currentKingBox.x,
                        currentKingBox.y
                    ) !in pieceServiceFactory.getPieceService(it.piece!!.pieceType).getThreateningLocations(updatedBoard, it)
                }
        }
        return false
    }

    fun performMove(move: Move, board: Board): Board {
        val boxes = board.boxes
        boxes[move.endX][move.endY].piece = boxes[move.startX][move.startY].piece
        boxes[move.startX][move.startY].piece = null
        return board.copy(boxes = boxes)
    }

    fun hasCurrentPlayerWon(): Boolean {
        TODO()
    }

    fun isDraw(): Boolean {
        TODO()
    }

    private fun getCurrentKingBox(board: Board, color: Color): Box {
        return board.boxes.flatten().find { it.piece?.pieceType == PieceType.KING && it.piece?.color == color }!!
    }
}
