package eventbus.domain

data class Event(
    val eventId: EventID,
    val name: String,
    val attributes: Map<String, Any>,
    val timestamp: Timestamp
)

enum class EventType {
    PRIORITY, LOGGING, ERROR
}
