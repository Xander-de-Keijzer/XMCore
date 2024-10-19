package nl.xandermarc.mc.ride.tracked

import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import nl.xandermarc.mc.lib.data.EntityData
import nl.xandermarc.mc.lib.data.EntityID
import nl.xandermarc.mc.lib.extensions.create
import nl.xandermarc.mc.lib.extensions.handle
import nl.xandermarc.mc.lib.extensions.item
import nl.xandermarc.mc.lib.packets.entities.AddEntityPacket
import nl.xandermarc.mc.lib.packets.entities.RemoveEntitiesPacket
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import nl.xandermarc.mc.ride.tracked.track.Track
import org.bukkit.Material
import org.bukkit.entity.Player

class TrackPreview(private val player: Player) {

    private val entities = mutableListOf<Int>()

    fun show(track: Track) {
        track.nodes.forEach {
            val id = EntityID.next
            AddEntityPacket(id, EntityType.ITEM_DISPLAY, it.value.location).sendPacket(player)
            SetEntityDataPacket(
                id,
                EntityData.ITEM_STACK_ID.create(item(Material.WHITE_CONCRETE).handle),
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