package parkinglot.service

import parkinglot.domain.ParkingSpotType
import parkinglot.repo.ParkingSpotRepository
import parkinglot.service.`parking-spots`.*

object ParkingSpotServiceFactory {

    private val parkingSpotServices: List<ParkingSpotService> =
        mutableListOf(CompactParkingSpotService(), LargeParkingSpotService(), ExtraLargeParkingSpotService(), MotorbikeParkingSpotService())
    private val registry: Map<ParkingSpotType, ParkingSpotService> = parkingSpotServices.associateBy { it.getParkingSpotType() }

    fun getParkingSpotService(parkingSpotType: ParkingSpotType): ParkingSpotService =
        registry[parkingSpotType] ?: throw IllegalArgumentException("No service found for $parkingSpotType")

    fun getParkingSpotService(parkingSpotId: String): ParkingSpotService {
        return registry[ParkingSpotRepository.getParkingSpot(parkingSpotId)!!.parkingSpotType]!!
    }
}