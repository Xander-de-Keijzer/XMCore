package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import nl.xandermarc.mc.lib.extensions.buildDefault
import nl.xandermarc.mc.lib.extensions.write
import nl.xandermarc.mc.lib.packets.Packet

class SetEntityDataPacket(
    id: Int,
    vararg values: SynchedEntityData.DataValue<*>
) : Packet<ClientboundSetEntityDataPacket>(ClientboundSetEntityDataPacket.STREAM_CODEC.buildDefault {
    writeVarInt(id)
    values.forEach { write(it) }
    writeByte(255)
})