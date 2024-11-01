package nl.xandermarc.mc.ride.entity

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.entities.TeleportEntityPacket
import nl.xandermarc.mc.lib.entities.UpdatableEntity
import org.joml.Quaterniond
import org.joml.Vector3d

abstract class RideEntity(
    type: EntityType<*>,
    location: Vector3d,
) : UpdatableEntity(type, location) {
    private var nextLocation: Vector3d? = null
    private val children = mutableListOf<RideEntity>()

    private var parent: RideEntity? = null
    private var offset: Vector3d? = null
    private var rotation: Quaterniond? = null

    override fun onUpdate() {
        nextLocation?.let {
            location = it
        }
        nextLocation = null
    }

    fun move(location: Vector3d) {
        addUpdate(TeleportEntityPacket(id, location))
        nextLocation = location
    }

    fun addChild(child: RideEntity, offset: Vector3d, rotation: Quaterniond) {
        child.parent = this
        child.offset = offset
        child.rotation = rotation
        children.add(child)
    }

    fun removeChild(child: RideEntity) {
        child.parent = null
        children.remove(child)
    }
}
