package parkinglot.service.`parking-spots`

import parkinglot.domain.ParkingSpotType
import parkinglot.domain.VehicleType

class ExtraLargeParkingSpotService: ParkingSpotService() {
    override fun getParkingSpotType(): ParkingSpotType {
        return ParkingSpotType.EXTRA_LARGE
    }

    override fun canVehicleFit(vehicleType: VehicleType): Boolean {
        return vehicleType in listOf(VehicleType.CAR, VehicleType.VAN, VehicleType.MOTORBIKE, VehicleType.TRUCK)
    }

    override fun getRatePerHour(): Double {
        return 4.0
    }
}