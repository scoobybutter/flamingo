package eventbus

import eventbus.exception.RetryLimitExceededException
import eventbus.domain.*
import eventbus.retry.RetryAlgorithm
import eventbus.util.KeyedExecutor
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.CopyOnWriteArrayList

class EventBus(
    threads: Int,
    retryAlgorithm: RetryAlgorithm<Event, Void>,
    timer: Timer,
    deadLetterQueue: EventBus? = null
) {
    private val executor: KeyedExecutor
    private val buses: MutableMap<Topic, MutableList<Event>>
    private val subscriptions: MutableMap<Topic, MutableSet<Subscription>>
    private val subscriberIndexes: MutableMap<Topic, MutableMap<EntityID, Index>>
    private val eventIndex: MutableMap<Topic, MutableMap<EventID, Index>>
    private val timestampIndex: MutableMap<Topic, ConcurrentSkipListMap<Timestamp, Index>>
    private val retryAlgorithm: RetryAlgorithm<Event, Void>
    private val deadLetterQueue: EventBus?
    private val timer: Timer

    init {
        executor = KeyedExecutor(threads)
        this.retryAlgorithm = retryAlgorithm
        this.deadLetterQueue = deadLetterQueue
        this.timer = timer
        this.buses = ConcurrentHashMap()
        this.subscriptions = ConcurrentHashMap()
        this.subscriberIndexes = ConcurrentHashMap()
        this.timestampIndex = ConcurrentHashMap()
        this.eventIndex = ConcurrentHashMap()
    }

    fun publish(topic: Topic, event: Event): CompletionStage<Void> {
        return executor.submit(topic.name) { addEventToBus(topic, event) }
    }

    fun poll(topic: Topic, subscriber: EntityID): CompletionStage<Event?> {
        return executor.get(topic.name + subscriber.id) {
            val index = subscriberIndexes[topic]!![subscriber]!!
            try {
                val event = buses[topic]!![index.value]
                subscriberIndexes[topic]!![subscriber] = Index(index.value.inc())
                return@get event
            } catch (exception: IndexOutOfBoundsException) {
                return@get null
            }
        }
    }

    fun registerTopic(topic: Topic) {
        buses[topic] = CopyOnWriteArrayList()
        subscriptions[topic] = Collections.newSetFromMap(ConcurrentHashMap())
        subscriberIndexes[topic] = ConcurrentHashMap<EntityID, Index>()
        timestampIndex[topic] = ConcurrentSkipListMap<Timestamp, Index>()
        eventIndex[topic] = ConcurrentHashMap<EventID, Index>()
    }

    fun subscribe(subscription: Subscription): CompletionStage<Void> {
        return executor.submit(subscription.topic.name) {
            val topic = subscription.topic
            subscriptions[topic]!!.add(subscription)
            subscriberIndexes[topic]!![subscription.subscriberId] = Index(buses[topic]!!.size)
        }
    }

    fun subscribeAfterTimestamp(topic: Topic, subscriber: EntityID, timestamp: Timestamp): CompletionStage<Void> {
        return executor.submit(topic.name + subscriber.id) {
            val entry = timestampIndex[topic]!!.higherEntry(timestamp)
            if (entry == null) {
                subscriberIndexes[topic]!![subscriber] = Index(buses[topic]!!.size)
            } else {
                val indexLessThanEquals = entry.value
                subscriberIndexes[topic]!![subscriber] = indexLessThanEquals
            }
        }
    }

    fun subscribeAfterEvent(topic: Topic, subscriber: EntityID, eventId: EventID): CompletionStage<Void> {
        return executor.submit(topic.name + subscriber.id) {
            val eIndex = eventIndex[topic]!![eventId]!!
            subscriberIndexes[topic]!![subscriber] = Index(eIndex.value.inc())
        }
    }

    fun getEvent(topic: Topic, eventId: EventID): CompletionStage<Event?> {
        return executor.get(topic.name) {
            val index = eventIndex[topic]!![eventId] ?: return@get null
            buses[topic]!![index.value]
        }
    }

    private fun addEventToBus(topic: Topic, event: Event) {
        val currentIndex = Index(buses[topic]!!.size)
        timestampIndex[topic]!![event.timestamp] = currentIndex
        eventIndex[topic]!![event.eventId] = currentIndex
        buses[topic]!!.add(event)
        subscriptions[topic]
            ?.filter { subscription: Subscription ->
                SubscriptionType.PUSH == subscription.subscriptionType
            }?.forEach { subscription: Subscription -> push(event, subscription) }
    }

    private fun push(event: Event, subscription: Subscription) {
        executor.submit(subscription.topic.name + subscription.subscriberId) {
            try {
                retryAlgorithm.attempt(subscription.eventHandler, event, 0)
            } catch (e: RetryLimitExceededException) {
                if (deadLetterQueue != null) {
                    deadLetterQueue.publish(
                        subscription.topic,
                        event
                    )
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}