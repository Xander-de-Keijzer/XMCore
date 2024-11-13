package nl.xandermarc.mc.ride.entity

import nl.xandermarc.mc.lib.packets.BundlePacket
import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

sealed class RideObject {
    abstract val offset: Vector3d
    private val children = mutableListOf<RideObject>()
    fun withChildren(vararg children: RideObject): RideObject {
        this.children.addAll(children)
        return this
    }
    fun create(): Packet<*> {
        val packets = mutableListOf(spawn())
        packets.addAll(children.map { it.spawn() })
        return BundlePacket(packets)
    }
    protected abstract fun spawn(): Packet<*>
}
