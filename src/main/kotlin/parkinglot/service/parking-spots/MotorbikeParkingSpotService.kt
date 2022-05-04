package parkinglot.service.`parking-spots`

import parkinglot.domain.ParkingSpotType
import parkinglot.domain.VehicleType

class MotorbikeParkingSpotService: ParkingSpotService() {
    override fun getParkingSpotType(): ParkingSpotType {
        return ParkingSpotType.MOTORBIKE
    }

    override fun canVehicleFit(vehicleType: VehicleType): Boolean {
        return vehicleType in listOf(VehicleType.MOTORBIKE)
    }

    override fun getRatePerHour(): Double {
        return 0.5
    }
}