package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import nl.xandermarc.mc.lib.extensions.build
import nl.xandermarc.mc.lib.packets.Packet
import org.joml.Vector3d

class MoveEntityPacket(
    id: Int,
    offset: Vector3d,
    onGround: Boolean = false,
) : Packet<ClientboundMoveEntityPacket>(ClientboundMoveEntityPacket.Pos.STREAM_CODEC.build {
    writeVarInt(id)
    writeShort((offset.x * 4096).toInt())
    writeShort((offset.y * 4096).toInt())
    writeShort((offset.z * 4096).toInt())
    writeBoolean(onGround)
}) {
    constructor(id: Int, from: Vector3d, to: Vector3d) : this(id, Vector3d(to).sub(from))
}
