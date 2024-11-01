package nl.xandermarc.mc.lib.utils

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TrackedList<T> {
    private val lock = ReentrantLock()
    private val _items = mutableListOf<T>()
    private val toBeAdded = mutableSetOf<T>()
    private val toBeRemoved = mutableSetOf<T>()
    val items: List<T> get() = _items.toList()

    fun add(item: T) = lock.withLock {
        toBeRemoved.remove(item)
        if (!_items.contains(item)) {
            toBeAdded.add(item)
        }
    }

    fun remove(item: T) = lock.withLock {
        toBeAdded.remove(item)
        if (_items.contains(item)) {
            toBeRemoved.add(item)
        }
    }

    fun getAdded() = lock.withLock {
        toBeAdded.toList().apply {
            _items.addAll(this)
            toBeAdded.clear()
        }
    }

    fun getRemoved() = lock.withLock {
        toBeRemoved.toList().apply {
            _items.removeAll(toBeRemoved)
            toBeRemoved.clear()
        }
    }

    fun clear() = lock.withLock {
        _items.clear()
        toBeAdded.clear()
        toBeRemoved.clear()
    }
}
