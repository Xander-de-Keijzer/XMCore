package nl.xandermarc.mc.lib.packets.entities

import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import nl.xandermarc.mc.lib.packets.AbstractPacket
import nl.xandermarc.mc.lib.packets.build

class RemoveEntitiesPacket(
    vararg ids: Int
) : AbstractPacket<ClientboundRemoveEntitiesPacket>(ClientboundRemoveEntitiesPacket.STREAM_CODEC.build {
    writeIntIdList(IntList.of(*ids))
})
