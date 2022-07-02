package eventbus.retry

import eventbus.exception.RetryLimitExceededException
import eventbus.exception.RetryableException
import java.util.function.Function

abstract class RetryAlgorithm<PARAMETER, RESULT>(
    private val maxAttempts: Int,
    private val retryTimeCalculator: Function<Int, Long>
) {
    fun attempt(task: Function<PARAMETER, RESULT>, parameter: PARAMETER, attempts: Int): RESULT {
        try {
            return task.apply(parameter)
        } catch (e: Exception) {
            if (e.cause is RetryableException) {
                if (attempts == maxAttempts) {
                    throw RetryLimitExceededException()
                } else {
                    try {
                        Thread.sleep(retryTimeCalculator.apply(attempts))
                        return attempt(task, parameter, attempts + 1)
                    } catch (interrupt: InterruptedException) {
                        throw RuntimeException(interrupt)
                    }
                }
            } else {
                throw RuntimeException(e)
            }
        }
    }
}