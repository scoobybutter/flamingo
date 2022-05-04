package parkinglot.domain

import java.time.LocalDateTime

data class ParkingTicket(
    val ticketId: String,
    val parkingSpotId: String,
    val vehicleNumber: String,
    val issuedAt: LocalDateTime,
    val status: ParkingTicketStatus,
    val payedAt: LocalDateTime? = null,
    val amount: Double? = null
)
