package parkinglot.domain

data class ParkingSpot(
    val id: String,
    val floorNumber: Int,
    val parkingSpotType: ParkingSpotType,
    val isFree: Boolean = true,
    val vehicle: Vehicle? = null
)
