package chess.domain

data class Box(
    val x: Int,
    val y: Int,
    var piece: Piece? = null
)
