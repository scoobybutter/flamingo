package cache.lru

class Node<K, V>(key: K?, value: V?) {
    val key: K?
    var value: V?
    var next: Node<K, V>? = null
    var prev: Node<K, V>? = null

    init {
        this.key = key
        this.value = value
    }
}