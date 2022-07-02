package eventbus.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

class KeyedExecutor(poolSize: Int) {
    private val executorPool: List<Executor>

    init {
        executorPool = (0..poolSize).map { Executors.newSingleThreadExecutor() }
    }

    fun submit(id: String, task: Runnable): CompletionStage<Void> {
        return CompletableFuture.runAsync(task, executorPool[id.hashCode() % executorPool.size])
    }

    fun <T> get(id: String, task: Supplier<T>): CompletionStage<T?> {
        return CompletableFuture.supplyAsync(task, executorPool[id.hashCode() % executorPool.size])
    }
}