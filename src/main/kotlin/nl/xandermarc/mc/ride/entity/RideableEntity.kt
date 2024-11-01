package nl.xandermarc.mc.ride.entity

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import org.joml.Vector3d

class RideableEntity(
    location: Vector3d
) : RideEntity(EntityType.ITEM_DISPLAY, location) {

    override fun onSpawn() = arrayOf(
        SetEntityDataPacket(id)
    )

}
