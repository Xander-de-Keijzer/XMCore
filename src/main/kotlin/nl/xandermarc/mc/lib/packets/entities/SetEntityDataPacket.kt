package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import nl.xandermarc.mc.lib.extensions.build
import nl.xandermarc.mc.lib.packets.AbstractPacket

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