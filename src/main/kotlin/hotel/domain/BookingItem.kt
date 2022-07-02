package hotel.domain

import java.time.LocalDateTime

data class BookingItem(
    val id: String,
    val occupyingUsers: List<User>,
    val roomId: String,
    val invoice: Invoice,
    val checkinAt: LocalDateTime? = null,
    val checkoutAt: LocalDateTime? = null,
    val status: RoomBookingStatus
)

enum class RoomBookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}
