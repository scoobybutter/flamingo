package chess.domain

data class Piece(
    val color: Color,
    val pieceType: PieceType
)

enum class Color {
    BLACK,
    WHITE
}

enum class PieceType {
    ROOK,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING,
    PAWN
}

val INITIAL_COORDINATES_PIECE_TYPE_MAP = mutableMapOf(
    Pair(0, 0) to PieceType.ROOK,
    Pair(1, 0) to PieceType.KNIGHT,
    Pair(2, 0) to PieceType.BISHOP,
    Pair(3, 0) to PieceType.QUEEN,
    Pair(4, 0) to PieceType.KING,
    Pair(5, 0) to PieceType.BISHOP,
    Pair(6, 0) to PieceType.KNIGHT,
    Pair(7, 0) to PieceType.ROOK,
    Pair(0, 1) to PieceType.PAWN,
    Pair(1, 1) to PieceType.PAWN,
    Pair(2, 1) to PieceType.PAWN,
    Pair(3, 1) to PieceType.PAWN,
    Pair(4, 1) to PieceType.PAWN,
    Pair(5, 1) to PieceType.PAWN,
    Pair(6, 1) to PieceType.PAWN,
    Pair(7, 1) to PieceType.PAWN,
)