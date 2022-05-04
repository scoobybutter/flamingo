package splitwise.domain

data class Expense(
    val id: String,
    val userIdBalanceMap: Map<String, Balance>,
    val groupId: String
)
