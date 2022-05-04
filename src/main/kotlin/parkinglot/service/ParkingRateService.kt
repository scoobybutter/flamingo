package parkinglot.service

import parkinglot.repo.ParkingSpotRepository
import parkinglot.repo.ParkingTicketRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ParkingRateService {
    fun getTicketAmount(ticketId: String): Double {
        val parkingTicket = ParkingTicketRepository.getParkingTicket(ticketId)!!
        val parkingSpot = ParkingSpotRepository.getParkingSpot(parkingTicket.parkingSpotId)!!
        return ChronoUnit.MINUTES.between(
            parkingTicket.issuedAt,
            LocalDateTime.now()
        ) * ParkingSpotServiceFactory
            .getParkingSpotService(parkingSpot.parkingSpotType)
            .getRatePerHour()
    }
}