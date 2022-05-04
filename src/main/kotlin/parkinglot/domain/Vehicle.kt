package parkinglot.domain

data class Vehicle(
    val licenseNumber: String,
    val vehicleType: VehicleType,
    val parkingTicket: ParkingTicket? = null
)
