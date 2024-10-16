package nl.xandermarc.mc.lib.entities.packets

import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import nl.xandermarc.mc.lib.entities.build

class RemoveEntitiesPacket(
    vararg ids: Int
) : Packet<ClientboundRemoveEntitiesPacket>(ClientboundRemoveEntitiesPacket.STREAM_CODEC.build {
    writeIntIdList(IntList.of(*ids))
})
