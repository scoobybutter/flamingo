package ratelimiter.exception

class RateLimitExceededException: IllegalStateException("Rate limit exceeded") {
}