package eventbus.domain

import java.util.function.Function

data class Subscription(
    val topic: Topic,
    val subscriberId: EntityID,
    val precondition: Function<Event, Boolean>,
    val eventHandler: Function<Event, Void>,
    val subscriptionType: SubscriptionType
)

enum class SubscriptionType {
    PULL, PUSH
}
