package hotel.domain

data class Invoice(
    val id: String,
    val invoiceItems: List<InvoiceItem>
)
