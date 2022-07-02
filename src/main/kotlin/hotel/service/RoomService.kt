package hotel.service

import hotel.domain.Room
import hotel.domain.RoomStatus
import hotel.domain.RoomType
import hotel.repo.RoomRepository
import java.util.UUID

class RoomService {
    fun addRoom(number: Int, roomType: RoomType, bookingPrice: Double) {
        val room = Room(
            id = UUID.randomUUID().toString(),
            roomType = roomType,
            number = number,
            status = RoomStatus.AVAILABLE,
            bookingPrice = bookingPrice
        )
        RoomRepository.addOrUpdateRoom(room.id, room)
    }

    fun markRoomUnavailable(roomId: String) {
        val room = RoomRepository.getRoom(roomId)!!
        RoomRepository.addOrUpdateRoom(roomId, room.copy(status = RoomStatus.NOT_AVAILABLE))
    }
}