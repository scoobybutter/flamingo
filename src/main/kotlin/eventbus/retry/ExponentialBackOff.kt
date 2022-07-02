package eventbus.retry

import kotlin.math.pow

class ExponentialBackOff<P, R>(maxAttempts: Int) :
    RetryAlgorithm<P, R>(maxAttempts, { attempts -> 2.0.pow((attempts - 1).toDouble()).toLong() * 1000 })