package cache.lru

import cache.Cache

class LRUCache<K, V>(capacity: Int) : Cache<K, V> {
    private val cache: MutableMap<K, Node<K, V>>
    private var size: Int = 0
    private val capacity: Int
    private val head: Node<K, V>
    private val tail: Node<K, V>

    init {
        require(capacity > 0) {
            "capacity should be greater than 0"
        }
        this.capacity = capacity
        this.cache = mutableMapOf()
        head = Node(null, null)
        tail = Node(null, null)

        head.next = tail
        tail.prev = head
    }

    private fun addCacheElementToHead(Node: Node<K, V>) {
        Node.prev = head
        Node.next = head.next

        head.next?.prev = Node
        head.next = Node
    }

    private fun removeCacheElement(Node: Node<K, V>) {
        val prev = Node.prev
        val next = Node.next

        next?.prev = prev
        prev?.next = next
    }

    private fun moveToHead(Node: Node<K, V>) {
        removeCacheElement(Node)
        addCacheElementToHead(Node)
    }

    private fun popTail(): Node<K, V> {
        val temp = tail.prev
        removeCacheElement(temp!!)
        return temp
    }

    override fun set(key: K, value: V) {
        if (cache[key] == null) {
            val newElement = Node(key, value)
            cache[key] = newElement
            size++
            if (size > capacity) {
                val poppedElement = popTail()
                cache.remove(poppedElement.key)
                size--
            }
        } else {
            cache[key]!!.value = value
            moveToHead(cache[key]!!)
        }
    }

    override fun get(key: K): V? {
        val res = cache[key] ?: return null
        moveToHead(res)
        return res.value
    }

    override fun size(): Int {
        return size
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }
}