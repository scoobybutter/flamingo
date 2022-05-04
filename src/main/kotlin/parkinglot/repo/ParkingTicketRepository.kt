package parkinglot.repo

import parkinglot.domain.ParkingTicket

object ParkingTicketRepository {
    private val ticketIdParkingTicketMap = emptyMap<String, ParkingTicket>().toMutableMap()

    fun addOrUpdateParkingTicket(ticketId: String, parkingTicket: ParkingTicket): ParkingTicket {
        ticketIdParkingTicketMap[ticketId] = parkingTicket
        return parkingTicket
    }

    fun getParkingTicket(ticketId: String) = ticketIdParkingTicketMap[ticketId]
}