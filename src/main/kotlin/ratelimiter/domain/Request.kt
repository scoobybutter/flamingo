package ratelimiter.domain

data class Request(
    val requestId: String,
    val startTime: Long
)
