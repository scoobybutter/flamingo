package cache.lfu

import cache.Cache
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap

class LFUCache<K, V>(capacity: Int) : Cache<K, V> {
    private val capacity: Int
    private var size = 0
    private val cache: MutableMap<K, Node<K, V>> = ConcurrentHashMap()
    private val countMap: MutableMap<K, Int> = ConcurrentHashMap()
    private val frequencyMap: MutableMap<Int, DoubleLinkedList<K, V>> = ConcurrentSkipListMap()

    init {
        this.capacity = capacity
    }


    override fun set(key: K, value: V) {
        if (cache[key] == null) {
            val newNode = Node(key, value)
            size++
            if (size > capacity) {
                val firstKey = frequencyMap.keys.first()
                val nodeToDelete = frequencyMap[firstKey]!!.getFirstNode()!!
                frequencyMap[firstKey]!!.remove(nodeToDelete)
                if (frequencyMap[firstKey]!!.getSize() == 0) {
                    frequencyMap.remove(firstKey)
                }
                countMap.remove(nodeToDelete.key)
                cache.remove(nodeToDelete.key)
                size--
            }
            frequencyMap.computeIfAbsent(1) { DoubleLinkedList() }.add(newNode)
            cache[key] = newNode
            countMap[key] = 1
        } else {
            val newNode = Node(key, value)
            val frequency = countMap[key]!!
            frequencyMap[frequency]!!.remove(cache[key]!!)
            if (frequencyMap[frequency]!!.getSize() == 0) {
                frequencyMap.remove(frequency)
            }
            cache[key] = newNode
            countMap[key] = frequency + 1
            frequencyMap.computeIfAbsent(frequency + 1) { DoubleLinkedList() }.add(newNode)
        }
    }

    override fun get(key: K): V? {
        val node = cache[key] ?: return null
        val newNode = Node(key, node.value)
        val frequency = countMap[key]!!
        frequencyMap[frequency]!!.remove(cache[key]!!)
        if (frequencyMap[frequency]!!.getSize() == 0) {
            frequencyMap.remove(frequency)
        }
        cache[key] = newNode
        countMap[key] = frequency + 1
        frequencyMap.computeIfAbsent(frequency + 1) { DoubleLinkedList() }.add(newNode)
        return node.value
    }

    override fun size(): Int {
        return size
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }
}