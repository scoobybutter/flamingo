package chess.service

import chess.domain.PieceType
import chess.service.pieces.*

class PieceServiceFactory {

    private val pieceServices: List<PieceService> =
        mutableListOf(BishopService(), KingService(), QueenService(), PawnService(), RookService(), KnightService())
    private val registry: Map<PieceType, PieceService> = pieceServices.associateBy { it.getPieceType() }

    fun getPieceService(pieceType: PieceType): PieceService =
        registry[pieceType] ?: throw IllegalArgumentException("No piece service found for $pieceType")
}
