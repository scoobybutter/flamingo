package hotel.service

import hotel.domain.ChargeType
import hotel.domain.InvoiceItem
import hotel.domain.InvoiceStatus
import hotel.repo.BookingRepository
import java.util.*

class InvoiceService {
    fun addInvoiceItem(
        roomId: String,
        bookingId: String,
        amount: Double,
        chargeType: ChargeType,
        status: InvoiceStatus
    ) {
        val booking = BookingRepository.getBooking(bookingId)!!
        BookingRepository.addOrUpdateBooking(bookingId, booking.copy(
            bookingItems = booking.bookingItems.map { bookingItem ->
                if (bookingItem.roomId == roomId) bookingItem.copy(
                    invoice = bookingItem.invoice.copy(
                        invoiceItems = bookingItem.invoice.invoiceItems + InvoiceItem(
                            id = UUID.randomUUID().toString(),
                            amount = amount,
                            chargeType = chargeType,
                            status = InvoiceStatus.UNPAID
                        )
                    )
                )
                else bookingItem
            }
        ))
    }

    fun markInvoiceItemPaid(roomId: String, bookingId: String, invoiceItemId: String) {
        val booking = BookingRepository.getBooking(bookingId)!!
        BookingRepository.addOrUpdateBooking(bookingId, booking.copy(
            bookingItems = booking.bookingItems.map { bookingItem ->
                if (bookingItem.roomId == roomId) bookingItem.copy(
                    invoice = bookingItem.invoice.copy(
                        invoiceItems = bookingItem.invoice.invoiceItems.map { invoiceItem ->
                            if (invoiceItem.id == invoiceItemId) invoiceItem.copy(status = InvoiceStatus.PAID)
                            else invoiceItem
                        }
                    )
                )
                else bookingItem
            }
        ))
    }

    fun markInvoicePaid(roomId: String, bookingId: String) {
        val booking = BookingRepository.getBooking(bookingId)!!
        BookingRepository.addOrUpdateBooking(bookingId, booking.copy(
            bookingItems = booking.bookingItems.map { bookingItem ->
                if (bookingItem.roomId == roomId) bookingItem.copy(
                    invoice = bookingItem.invoice.copy(
                        invoiceItems = bookingItem.invoice.invoiceItems.map { invoiceItem ->
                            invoiceItem.copy(status = InvoiceStatus.PAID)
                        }
                    )
                )
                else bookingItem
            }
        ))
    }
}