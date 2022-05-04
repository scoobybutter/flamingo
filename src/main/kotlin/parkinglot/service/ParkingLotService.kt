package parkinglot.service

import parkinglot.domain.ParkingSpot
import parkinglot.domain.ParkingSpotType
import parkinglot.domain.Vehicle
import parkinglot.repo.ParkingSpotRepository
import parkinglot.repo.ParkingTicketRepository
import java.util.*

object ParkingLotService {
    private val parkingRateService = ParkingRateService()
    private val paymentService = PaymentService()

    fun createParkingLot() {
        createParkingFloor(0, 5, 10, 15, 20)
        createParkingFloor(1, 15, 5, 5, 10)
    }

    fun assignParkingSpot(vehicle: Vehicle): ParkingSpot {
        return ParkingSpotRepository.getAllParkingSpot()
            .first {
                it.isFree && ParkingSpotServiceFactory
                    .getParkingSpotService(it.parkingSpotType)
                    .canVehicleFit(vehicle.vehicleType)
            }
    }

    fun parkVehicle(vehicle: Vehicle, parkingSpotId: String) =
        ParkingSpotServiceFactory.getParkingSpotService(parkingSpotId).parkVehicle(vehicle, parkingSpotId)

    fun getTicketAmount(ticketId: String): Double {
        return parkingRateService.getTicketAmount(
            ticketId
        )
    }

    fun payParkingTicket(ticketId: String, amount: Double) = paymentService.completePayment(ticketId, amount)

    fun releaseVehicle(ticketId: String) {
        val parkingTicket = ParkingTicketRepository.getParkingTicket(ticketId)!!
        ParkingSpotServiceFactory.getParkingSpotService(parkingTicket.parkingSpotId)
            .releaseVehicle(parkingTicket.parkingSpotId)
    }

    private fun createParkingFloor(
        floorNumber: Int,
        motorcycleSpots: Long,
        compactSpots: Long,
        largeSpots: Long,
        extraLargeSpots: Long
    ) {
        val parkingSpots = (0..motorcycleSpots).map {
            createParkingSpot(floorNumber, ParkingSpotType.MOTORBIKE)
        }.toMutableList()
        parkingSpots += (0..compactSpots).map {
            createParkingSpot(floorNumber, ParkingSpotType.COMPACT)
        }
        parkingSpots += (0..largeSpots).map {
            createParkingSpot(floorNumber, ParkingSpotType.LARGE)
        }
        parkingSpots += (0..extraLargeSpots).map {
            createParkingSpot(floorNumber, ParkingSpotType.EXTRA_LARGE)
        }
    }

    private fun createParkingSpot(
        floorNumber: Int,
        type: ParkingSpotType
    ) = ParkingSpot(
        id = UUID.randomUUID().toString(),
        floorNumber = floorNumber,
        parkingSpotType = type
    )
}

