package parkinglot.service.`parking-spots`

import parkinglot.domain.*
import parkinglot.repo.ParkingSpotRepository
import parkinglot.repo.ParkingTicketRepository
import java.time.LocalDateTime
import java.util.*

abstract class ParkingSpotService {
    abstract fun getParkingSpotType(): ParkingSpotType
    abstract fun canVehicleFit(vehicleType: VehicleType): Boolean
    abstract fun getRatePerHour(): Double

    fun parkVehicle(vehicle: Vehicle, parkingSpotId: String): ParkingTicket {
        val parkingSpot = ParkingSpotRepository.getParkingSpot(parkingSpotId)!!
        val parkingTicket = ParkingTicket(
            ticketId = UUID.randomUUID().toString(),
            parkingSpotId = parkingSpotId,
            vehicleNumber = vehicle.licenseNumber,
            issuedAt = LocalDateTime.now(),
            status = ParkingTicketStatus.ACTIVE
        )
        ParkingSpotRepository.addOrUpdateParkingSpot(
            parkingSpotId, parkingSpot.copy(
                vehicle = vehicle.copy(parkingTicket = parkingTicket),
                isFree = false
            )
        )
        ParkingTicketRepository.addOrUpdateParkingTicket(parkingTicket.ticketId, parkingTicket)
        return parkingTicket
    }

    fun releaseVehicle(parkingSpotId: String) {
        val parkingSpot = ParkingSpotRepository.getParkingSpot(parkingSpotId)!!
        ParkingSpotRepository.addOrUpdateParkingSpot(
            parkingSpot.id, parkingSpot.copy(
                vehicle = null,
                isFree = true
            )
        )
    }
}