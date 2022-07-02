package hotel.domain

data class Room(
    val id: String,
    val number: Int,
    val roomType: RoomType,
    val bookingPrice: Double,
    val status: RoomStatus
)