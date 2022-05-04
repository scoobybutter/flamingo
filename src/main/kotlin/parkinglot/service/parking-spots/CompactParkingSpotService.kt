package parkinglot.service.`parking-spots`

import parkinglot.domain.ParkingSpotType
import parkinglot.domain.VehicleType

class CompactParkingSpotService: ParkingSpotService() {
    override fun getParkingSpotType(): ParkingSpotType {
        return ParkingSpotType.COMPACT
    }

    override fun canVehicleFit(vehicleType: VehicleType): Boolean {
        return vehicleType in listOf(VehicleType.MOTORBIKE, VehicleType.VAN)
    }

    override fun getRatePerHour(): Double {
        return 1.0
    }
}