package hotel.domain

data class Hotel(
    val name: String,
    val rooms: List<Room>,
    val bookings: List<Booking>
)