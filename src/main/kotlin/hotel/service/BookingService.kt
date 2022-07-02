package hotel.service

import hotel.domain.*
import hotel.repo.BookingRepository
import hotel.repo.RoomRepository
import java.time.LocalDateTime
import java.util.UUID

class BookingService {
    fun reserveRooms(roomIds: List<String>, user: User) {
        val rooms = roomIds.map { RoomRepository.getRoom(it)!! }
        synchronized(this) {
            require(rooms.all { it.status in listOf(RoomStatus.AVAILABLE) })
            val booking = Booking(
                id = UUID.randomUUID().toString(),
                bookingItems = rooms.map {
                    BookingItem(
                        id = UUID.randomUUID().toString(),
                        occupyingUsers = emptyList(),
                        roomId = it.id,
                        status = RoomBookingStatus.CONFIRMED,
                        invoice = Invoice(
                            id = UUID.randomUUID().toString(),
                            invoiceItems = listOf(
                                InvoiceItem(
                                    id = UUID.randomUUID().toString(),
                                    amount = it.bookingPrice,
                                    chargeType = ChargeType.ROOM_CHARGE,
                                    status = InvoiceStatus.PAID
                                )
                            )
                        )
                    )
                },
                user = user
            )
            BookingRepository.addOrUpdateBooking(booking.id, booking)
            rooms.forEach { RoomRepository.addOrUpdateRoom(it.id, it.copy(status = RoomStatus.RESERVED)) }
        }
    }

    fun checkInRoom(roomId: String, bookingId: String, users: List<User>) {
        val room = RoomRepository.getRoom(roomId)!!
        val booking = BookingRepository.getBooking(bookingId)!!
        BookingRepository.addOrUpdateBooking(bookingId, booking.copy(
            bookingItems = booking.bookingItems.map {
                if (it.roomId == roomId) it.copy(
                    status = RoomBookingStatus.CHECKED_IN,
                    checkinAt = LocalDateTime.now(),
                    occupyingUsers = users
                )
                else it
            }
        ))
        RoomRepository.addOrUpdateRoom(roomId, room.copy(status = RoomStatus.OCCUPIED))
    }

    fun checkoutRoom(roomId: String, bookingId: String) {
        val room = RoomRepository.getRoom(roomId)!!
        val booking = BookingRepository.getBooking(bookingId)!!
        require(booking.bookingItems.first { it.roomId == roomId }.invoice.invoiceItems.all { it.status == InvoiceStatus.PAID })
        BookingRepository.addOrUpdateBooking(bookingId, booking.copy(
            bookingItems = booking.bookingItems.map {
                if (it.roomId == roomId) it.copy(
                    status = RoomBookingStatus.CHECKED_OUT,
                    checkoutAt = LocalDateTime.now()
                )
                else it
            }
        ))
        RoomRepository.addOrUpdateRoom(roomId, room.copy(status = RoomStatus.AVAILABLE))
    }
}