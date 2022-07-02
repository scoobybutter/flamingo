package ratelimiter.util

import java.util.concurrent.TimeUnit

class Timer {
    fun getCurrentTime(timeUnit: TimeUnit): Long {
        return timeUnit.convert(getCurrentTimeInMillis(), TimeUnit.MILLISECONDS)
    }

    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }
}