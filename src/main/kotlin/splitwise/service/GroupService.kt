package splitwise.service

import splitwise.domain.*
import splitwise.repo.GroupRepository
import java.util.UUID

class GroupService {
    fun createGroup(name: String, users: List<User>): Group {
        val group = Group(
            id = UUID.randomUUID().toString(),
            name = name,
            users = users,
            expenses = emptyList()
        )
        return GroupRepository.addOrUpdateGroup(group.id, group)
    }

    fun addExpense(groupId: String, balanceMap: Map<String, Balance>) {
        val group = GroupRepository.getGroup(groupId)
        GroupRepository.addOrUpdateGroup(
            groupId,
            group.copy(
                expenses = group.expenses + Expense(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    userIdBalanceMap = balanceMap
                )
            )
        )
    }

    fun getExpenses(groupId: String) = GroupRepository.getGroup(groupId).expenses

    fun getSettlementMap(groupId: String): List<Settlement> {
        val group = GroupRepository.getGroup(groupId)
        return ExpenseService.getSettlement(group.expenses)
    }
}