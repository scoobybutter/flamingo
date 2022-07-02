package cache.lru

fun main() {
    val cache = LRUCache<Int, Int>(capacity = 3)
    cache[1] = 3
    cache[2] = 5
    println(cache[1])
    cache[5] = 6
    cache[8] = 2
    println(cache[5])
    println(cache[8])
    println(cache[1])
}