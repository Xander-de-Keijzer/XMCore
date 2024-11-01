package nl.xandermarc.mc.ride.entity

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.data.EntityData
import nl.xandermarc.mc.lib.extensions.create
import nl.xandermarc.mc.lib.extensions.handle
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import org.bukkit.inventory.ItemStack
import org.joml.Vector3d

class DisplayEntity(
    location: Vector3d,
    private var item: ItemStack
) : RideEntity(EntityType.ITEM_DISPLAY, location) {
    private var nextItem: ItemStack? = null

    override fun onUpdate() {
        super.onUpdate()
        nextItem?.let {
            item = it
        }
        nextItem = null
    }

    override fun onSpawn() = arrayOf(
        SetEntityDataPacket(
            id,
            EntityData.ITEM_STACK_ID.create(item.handle)
        )
    )

    fun setItem(item: ItemStack) {
        addUpdate(SetEntityDataPacket(
            id,
            EntityData.ITEM_STACK_ID.create(item.handle)
        ))
        nextItem = item
    }

}
