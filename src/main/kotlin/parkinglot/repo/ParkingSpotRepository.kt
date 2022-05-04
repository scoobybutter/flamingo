package parkinglot.repo

import parkinglot.domain.ParkingSpot


object ParkingSpotRepository {

    private val spotIdParkingSpotMap = emptyMap<String, ParkingSpot>().toMutableMap()

    fun addOrUpdateParkingSpot(spotId: String, parkingSpot: ParkingSpot): ParkingSpot {
        spotIdParkingSpotMap[spotId] = parkingSpot
        return parkingSpot
    }

    fun getParkingSpot(spotId: String) = spotIdParkingSpotMap[spotId]

    fun getAllParkingSpot() = spotIdParkingSpotMap.values
}