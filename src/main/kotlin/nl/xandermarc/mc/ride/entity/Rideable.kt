package nl.xandermarc.mc.ride.entity

import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

data class Rideable(
    override val offset: Vector3d
) : RideObject() {
    override fun spawn(): Packet<*> {
        TODO("Not yet implemented")
    }
}
