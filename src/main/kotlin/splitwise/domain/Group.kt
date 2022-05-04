package splitwise.domain

data class Group(
    val id: String,
    val name: String,
    val users: List<User>,
    val expenses: List<Expense>
)
