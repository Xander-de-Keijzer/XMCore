package nl.xandermarc.mc.lib.entities

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.area.TrackedArea
import nl.xandermarc.mc.lib.extensions.toVector3d
import nl.xandermarc.mc.lib.extensions.with
import nl.xandermarc.mc.lib.packets.BundlePacket
import org.bukkit.entity.Player

class EntityArea<T : Area>(
    area: T,
) {
    private val trackedArea = TrackedArea<T, Player>(area) { it.location.toVector3d() }
    val entities: MutableList<Entity> = mutableListOf()
    var area get() = trackedArea.area
        set(value) { trackedArea.area = value }

    fun add(vararg entity: Entity) = addAll(entity.toList())
    fun addAll(entities: Iterable<Entity>) {
        this.entities.addAll(entities)
        trackedArea.items.with(entities) { showTo(it) }
    }

    fun remove(vararg entity: Entity) = removeAll(entity.toList())
    fun removeAll(entities: Iterable<Entity>) {
        this.entities.removeAll(entities.toSet())
        trackedArea.items.with(entities) { removeFrom(it) }
    }

    fun update(players: Iterable<Player> = Globals.players) {
        trackedArea.update(players)
        trackedArea.added.with(entities) { showTo(it) }
        trackedArea.removed.with(entities) { removeFrom(it) }
        BundlePacket(
            entities.filterIsInstance<MutableEntity>().flatMap { it.getUpdates() }
        ).send(trackedArea.items)
    }

    fun remove() {
        trackedArea.items.with(entities) { removeFrom(it) }
        trackedArea.removed.with(entities) { removeFrom(it) }
        trackedArea.added.with(entities) { removeFrom(it) }
        trackedArea.remove()
        entities.clear()
    }
}
