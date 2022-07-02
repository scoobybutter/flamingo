package cache.lfu

fun main() {
    val cache = LFUCache<Int, Int>(2)
    cache[1] = 1
    cache[2] = 2
    println(cache[1])
    println(cache[1])
    println(cache[1])
    cache[2] = 20
    println(cache[2])
    println(cache[2])
    cache[3] = 3
    println(cache[2])
    println(cache[1])
    println(cache[3])
}