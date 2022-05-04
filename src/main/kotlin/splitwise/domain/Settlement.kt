package splitwise.domain

data class Settlement(
    val fromUserId: String,
    val toUserId: String,
    val amount: Int
)
