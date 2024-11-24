package nl.xandermarc.mc.lib.packets.entities

import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import nl.xandermarc.mc.lib.extensions.build
import nl.xandermarc.mc.lib.packets.Packet

class RemoveEntitiesPacket(
    vararg ids: Int
) : Packet<ClientboundRemoveEntitiesPacket>(ClientboundRemoveEntitiesPacket.STREAM_CODEC.build {
    writeIntIdList(IntList.of(*ids))
})
