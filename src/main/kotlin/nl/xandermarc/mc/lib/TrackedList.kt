package nl.xandermarc.mc.lib

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class TrackedList<T> {
    private val lock = ReentrantReadWriteLock()
    private val _items = mutableListOf<T>()
    private val toBeAdded = mutableSetOf<T>()
    private val toBeRemoved = mutableSetOf<T>()
    val items: List<T> get() = lock.read { _items.toList() }

    fun add(item: T) = lock.write {
        toBeRemoved.remove(item)
        if (!_items.contains(item)) {
            toBeAdded.add(item)
        }
    }

    fun remove(item: T) = lock.write {
        toBeAdded.remove(item)
        if (_items.contains(item)) {
            toBeRemoved.add(item)
        }
    }

    fun getAdded() = lock.write {
        toBeAdded.toList().apply {
            _items.addAll(this)
            toBeAdded.clear()
        }
    }

    fun getRemoved() = lock.write {
        toBeRemoved.toList().apply {
            _items.removeAll(toBeRemoved)
            toBeRemoved.clear()
        }
    }

    fun clear() = lock.write {
        _items.clear()
        toBeAdded.clear()
        toBeRemoved.clear()
    }
}
