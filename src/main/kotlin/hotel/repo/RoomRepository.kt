package hotel.repo

import hotel.domain.Room

object RoomRepository {
    private val roomIdRoomMap = mapOf<String, Room>().toMutableMap()

    fun addOrUpdateRoom(roomId: String, room: Room) {
        roomIdRoomMap[roomId] = room
    }

    fun getRoom(roomId: String) = roomIdRoomMap[roomId]
}