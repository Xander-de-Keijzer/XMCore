package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import nl.xandermarc.mc.lib.packets.AbstractPacket
import nl.xandermarc.mc.lib.packets.build

class SetEntityDataPacket(
    id: Int,
    vararg values: SynchedEntityData.DataValue<*>
) : AbstractPacket<ClientboundSetEntityDataPacket>(ClientboundSetEntityDataPacket.STREAM_CODEC.build {
    writeVarInt(id)
    values.forEach {
        it.write(this)
    }
    writeByte(255)
})