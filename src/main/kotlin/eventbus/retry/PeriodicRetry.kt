package eventbus.retry

class PeriodicRetry<P, R>(
    maxAttempts: Int,
    waitTimeInMillis: Long
) : RetryAlgorithm<P, R>(maxAttempts, { waitTimeInMillis })