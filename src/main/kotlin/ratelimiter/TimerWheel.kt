package ratelimiter

import ratelimiter.domain.Request
import ratelimiter.exception.RateLimitExceededException
import ratelimiter.util.Timer
import java.util.concurrent.*

class TimerWheel(
    private val timeOutPeriod: Int,
    private val capacityPerSlot: Int,
    private val timeUnit: TimeUnit,
    private val timer: Timer
) {
    private val slots: List<ArrayBlockingQueue<Request>>
    private val threads: List<ExecutorService>

    init {
        slots = ArrayList(timeOutPeriod)
        threads = ArrayList(timeOutPeriod)
        for (i in slots.indices) {
            slots[i] = ArrayBlockingQueue(capacityPerSlot)
            threads[i] = Executors.newSingleThreadExecutor()
        }
        val timePerSlotInMillis = TimeUnit.MILLISECONDS.convert(1, timeUnit)
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                { this.flushRequests() },
                timePerSlotInMillis - (timer.getCurrentTimeInMillis() % timePerSlotInMillis),
                timePerSlotInMillis,
                TimeUnit.MILLISECONDS
            )
    }

    fun flushRequests(): Future<*> {
        val currentSlot = getCurrentSlot()
        return threads[currentSlot].submit {
            for (request in slots[currentSlot]) {
                if (timer.getCurrentTime(timeUnit) - request.startTime >= timeOutPeriod) {
                    slots[currentSlot].remove(request)
                }
            }
        }
    }

    fun addRequest(request: Request): Future<*> {
        val currentSlot = getCurrentSlot()
        return threads[currentSlot].submit {
            if (slots[currentSlot].size >= capacityPerSlot) {
                throw RateLimitExceededException()
            }
            slots[currentSlot].add(request)
        }
    }

    private fun getCurrentSlot(): Int {
        return timer.getCurrentTime(timeUnit).toInt() % slots.size
    }
}