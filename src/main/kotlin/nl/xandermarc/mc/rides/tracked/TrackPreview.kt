package nl.xandermarc.mc.rides.tracked

import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import nl.xandermarc.mc.lib.entities.*
import nl.xandermarc.mc.lib.entities.packets.AddEntityPacket
import nl.xandermarc.mc.lib.entities.packets.RemoveEntitiesPacket
import nl.xandermarc.mc.lib.entities.packets.SetEntityDataPacket
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TrackPreview(private val player: Player) {

    private val entities = mutableListOf<Int>()

    fun show(track: Track) {
        track.nodes.forEach {
            val id = EntityID.next
            AddEntityPacket(id, EntityType.ITEM_DISPLAY, it.value.location).packet.sendPacket(player)
            SetEntityDataPacket(
                id,
                EntityData.ITEM_STACK_ID.create(ItemStack(Material.WHITE_CONCRETE).nms()),
                EntityData.ITEM_DISPLAY_ID.create(ItemDisplayContext.HEAD.id),
            ).packet.sendPacket(player)
            entities.add(id)
        }
    }

    fun remove() {
        RemoveEntitiesPacket(*entities.toIntArray()).packet.sendPacket(player)
        entities.clear()
    }

}