package chess.domain

data class Game(
    val id: Long = -1,
    val board: Board,
    val status: GameStatus,
    val playerOne: Player,
    val playerTwo: Player
)

enum class GameStatus {
    WHITE_MOVE,
    BLACK_MOVE,
    WHITE_VICTORY,
    BLACK_VICTORY,
    DRAW
}