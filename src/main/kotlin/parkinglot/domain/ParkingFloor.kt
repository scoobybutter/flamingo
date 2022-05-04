package parkinglot.domain

data class ParkingFloor(
    val floorNumber: Int,
    val parkingSpots: List<ParkingSpot>
)
