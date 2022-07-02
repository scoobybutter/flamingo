package hotel.domain

data class InvoiceItem(
    val id: String,
    val amount: Double,
    val chargeType: ChargeType,
    val status: InvoiceStatus
)

enum class ChargeType {
    ROOM_CHARGE,
    LAUNDRY,
    FOOD,
}

enum class InvoiceStatus {
    PAID,
    UNPAID
}
