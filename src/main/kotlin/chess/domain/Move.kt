package chess.domain

data class Move(
    val gameId: Long,
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val player: Player
)
