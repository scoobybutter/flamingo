package chess.domain

data class Board(
    val size: Int,
    val boxes: List<List<Box>>
)

val DEFAULT_SIZE = 8
