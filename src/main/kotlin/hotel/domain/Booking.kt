package hotel.domain

data class Booking(
    val id: String,
    val bookingItems: List<BookingItem>,
    val user: User
)