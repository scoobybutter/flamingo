package parkinglot.service

import parkinglot.domain.ParkingTicketStatus
import parkinglot.repo.ParkingTicketRepository
import java.time.LocalDateTime

class PaymentService {
    fun completePayment(ticketId: String, amount: Double): Boolean {
        val parkingTicket = ParkingTicketRepository.getParkingTicket(ticketId)!!
        ParkingTicketRepository.addOrUpdateParkingTicket(
            ticketId,
            parkingTicket.copy(amount = amount, payedAt = LocalDateTime.now(), status = ParkingTicketStatus.PAID)
        )
        return true
    }
}