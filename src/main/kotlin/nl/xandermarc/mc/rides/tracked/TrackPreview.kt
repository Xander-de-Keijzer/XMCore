package nl.xandermarc.mc.rides.tracked

import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import nl.xandermarc.mc.lib.packets.create
import nl.xandermarc.mc.lib.packets.entities.AddEntityPacket
import nl.xandermarc.mc.lib.packets.entities.RemoveEntitiesPacket
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import nl.xandermarc.mc.lib.packets.entities.objects.EntityData
import nl.xandermarc.mc.lib.packets.entities.objects.EntityID
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TrackPreview(private val player: Player) {

    private val entities = mutableListOf<Int>()

    fun show(track: Track) {
        track.nodes.forEach {
            val id = EntityID.next
            AddEntityPacket(id, EntityType.ITEM_DISPLAY, it.value.location).sendPacket(player)
            SetEntityDataPacket(
                id,
                EntityData.ITEM_STACK_ID.create(ItemStack(Material.WHITE_CONCRETE)),
                EntityData.ITEM_DISPLAY_ID.create(ItemDisplayContext.HEAD.id),
            ).sendPacket(player)
            entities.add(id)
        }
    }

    fun remove() {
        RemoveEntitiesPacket(*entities.toIntArray()).sendPacket(player)
        entities.clear()
    }

}