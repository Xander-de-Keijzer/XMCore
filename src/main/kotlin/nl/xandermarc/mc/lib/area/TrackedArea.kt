package nl.xandermarc.mc.lib.area

import nl.xandermarc.mc.lib.utils.TrackedList
import org.joml.Vector3d

class TrackedArea<T : Area, U>(
    var area: T,
    private val itemPosition: (U) -> Vector3d
) {
    private val tracked: TrackedList<U> = TrackedList()
    val items: Iterable<U> = tracked.items
    val added get() = tracked.getAdded()
    val removed get() = tracked.getRemoved()
    fun remove() = tracked.clear()

    fun update(items: Iterable<U>) {
        for (item in items) {
            if (area.contains(itemPosition(item))) {
                tracked.add(item)
            } else {
                tracked.remove(item)
            }
        }
    }
}
