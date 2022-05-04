package parkinglot.service.`parking-spots`

import parkinglot.domain.ParkingSpotType
import parkinglot.domain.VehicleType

class LargeParkingSpotService: ParkingSpotService() {
    override fun getParkingSpotType(): ParkingSpotType {
        return ParkingSpotType.LARGE
    }

    override fun canVehicleFit(vehicleType: VehicleType): Boolean {
        return vehicleType in listOf(VehicleType.MOTORBIKE, VehicleType.CAR, VehicleType.VAN)
    }

    override fun getRatePerHour(): Double {
        return 2.0
    }
}