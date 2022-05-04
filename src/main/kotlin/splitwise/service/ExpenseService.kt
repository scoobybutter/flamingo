package splitwise.service

import splitwise.domain.Expense
import splitwise.domain.Settlement
import java.util.*
import kotlin.math.absoluteValue

object ExpenseService {
    fun getSettlement(expenses: List<Expense>): List<Settlement> {
        val userIdBalanceMap = getUserIdBalanceAmountMap(expenses)
        val positiveBalances = PriorityQueue { t1: Node, t2: Node -> t1.finalBalance - t2.finalBalance }
        val negativeBalances = PriorityQueue { t1: Node, t2: Node -> t1.finalBalance - t2.finalBalance }
        userIdBalanceMap.filter { it.value > 0 }
            .forEach { positiveBalances.add(Node(userId = it.key, finalBalance = it.value)) }
        userIdBalanceMap.filter { it.value < 0 }
            .forEach { negativeBalances.add(Node(userId = it.key, finalBalance = it.value.absoluteValue)) }
        val result = listOf<Settlement>().toMutableList()
        while (positiveBalances.isNotEmpty()) {
            val receiver = positiveBalances.poll()
            val sender = negativeBalances.poll()
            val amountTransferred = minOf(receiver.finalBalance, sender.finalBalance)
            result.add(Settlement(fromUserId = sender.userId, toUserId = receiver.userId, amount = amountTransferred))
            sender.finalBalance -= amountTransferred
            receiver.finalBalance -= amountTransferred
            if (sender.finalBalance != 0) negativeBalances.add(sender)
            if (receiver.finalBalance != 0) positiveBalances.add(receiver)
        }
        return result
    }

    private fun getUserIdBalanceAmountMap(expenses: List<Expense>): Map<String, Int> {
        val userIdBalanceMap = mapOf<String, Int>().toMutableMap()
        expenses
            .map { expense -> expense.userIdBalanceMap.map { it.key to it.value.amount }.toMap() }
            .forEach { balanceMap ->
                balanceMap.map {
                    userIdBalanceMap[it.key] = userIdBalanceMap.getOrDefault(it.key, 0) + it.value
                }
            }
        return userIdBalanceMap
    }
}

data class Node(
    val userId: String,
    var finalBalance: Int
)