package nl.xandermarc.mc.lib.entities

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d
import java.util.concurrent.atomic.AtomicReference

abstract class MutableEntity(
    type: EntityType<*>,
    location: Vector3d
) : Entity(type, location) {
    private val updates = AtomicReference(mutableListOf<Packet<*>>())
    protected fun addUpdate(packet: Packet<*>) { updates.get().add(packet) }
    protected fun addUpdates(vararg packets: Packet<*>) { updates.get().addAll(packets) }
    protected fun addUpdates(packets: Iterable<Packet<*>>) { updates.get().addAll(packets) }
    fun getUpdates(): MutableList<Packet<*>> = updates.getAndSet(mutableListOf())
}
