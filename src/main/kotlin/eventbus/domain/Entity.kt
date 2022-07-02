package eventbus.domain

data class Entity(
    val entityId: EntityID,
    val name: String,
    val ipAddress: String
)