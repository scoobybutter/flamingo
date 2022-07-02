package cache.lfu

class DoubleLinkedList<K, V> {
    private var size = 0
    private val head: Node<K, V> = Node(null, null)
    private val tail: Node<K, V> = Node(null, null)

    init {
        head.next = tail
        tail.prev = head
    }

    fun getFirstNode() = head.next

    fun add(node: Node<K, V>) {
        node.next = tail
        node.prev = tail.prev

        tail.prev?.next = node
        tail.prev = node
        size++
    }

    fun remove(node: Node<K, V>) {
        val prev = node.prev
        prev?.next = node.next?.next
        node.next?.prev = prev
        size--
    }

    fun getSize() = size
}