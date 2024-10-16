package nl.xandermarc.mc.lib.entities.packets

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import nl.xandermarc.mc.lib.entities.build

class SetEntityDataPacket(
    id: Int,
    vararg values: SynchedEntityData.DataValue<*>
) : Packet<ClientboundSetEntityDataPacket>(ClientboundSetEntityDataPacket.STREAM_CODEC.build {
    writeVarInt(id)
    values.forEach {
        it.write(this)
    }
    writeByte(255)
})