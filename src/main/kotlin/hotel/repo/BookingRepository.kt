package hotel.repo

import hotel.domain.Booking

object BookingRepository {
    private val bookingIdBookingMap = mapOf<String, Booking>().toMutableMap()

    fun addOrUpdateBooking(bookingId: String, booking: Booking) {
        bookingIdBookingMap[bookingId] = booking
    }

    fun getBooking(bookingId: String) = bookingIdBookingMap[bookingId]
}