package nl.xandermarc.mc.lib.entities

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

abstract class UpdatableEntity(
    type: EntityType<*>,
    location: Vector3d
) : Entity(type, location) {
    private val _updates = mutableListOf<Packet<*>>()
    protected fun addUpdate(packet: Packet<*>) { _updates.add(packet) }
    protected fun addUpdates(vararg packets: Packet<*>) { _updates.addAll(packets) }
    protected fun addUpdates(packets: Iterable<Packet<*>>) { _updates.addAll(packets) }
    val updates: List<Packet<*>>
        get() = _updates.toList().also { _updates.clear(); onUpdate() }
    abstract fun onUpdate()
}
