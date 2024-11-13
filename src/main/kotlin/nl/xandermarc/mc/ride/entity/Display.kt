package nl.xandermarc.mc.ride.entity

import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

data class Display(
    override val offset: Vector3d,
    val model: Int
) : RideObject() {
    override fun spawn(): Packet<*> {
        TODO("Not yet implemented")
    }
}
