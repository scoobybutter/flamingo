package splitwise.repo

import splitwise.domain.Group

object GroupRepository {
    private val groupIdGroupMap = mapOf<String, Group>().toMutableMap()

    fun addOrUpdateGroup(groupId: String, group: Group): Group {
        groupIdGroupMap[groupId] = group
        return group
    }

    fun getGroup(groupId: String) = groupIdGroupMap[groupId] ?: error("Group $groupId not found")
}