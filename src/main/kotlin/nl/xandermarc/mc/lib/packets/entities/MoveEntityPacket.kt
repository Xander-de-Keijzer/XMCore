package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import nl.xandermarc.mc.lib.extensions.build
import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

class MoveEntityPacket(
    id: Int,
    previous: Vector3d,
    current: Vector3d,
    onGround: Boolean = false,
) : Packet<ClientboundMoveEntityPacket>(ClientboundMoveEntityPacket.Pos.STREAM_CODEC.build {
    writeVarInt(id)
    writeShort((current.x * 4096 - previous.x * 4096).toInt())
    writeShort((current.y * 4096 - previous.y * 4096).toInt())
    writeShort((current.z * 4096 - previous.z * 4096).toInt())
    writeBoolean(onGround)
})
