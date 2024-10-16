package nl.xandermarc.mc.lib.entities.packets

import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import nl.xandermarc.mc.lib.entities.build
import nl.xandermarc.mc.lib.entities.writeVector3d
import org.joml.Vector3d
import kotlin.math.floor

class TeleportEntityPacket(
    id: Int,
    location: Vector3d,
    yaw: Float = 0f,
    pitch: Float = 0f,
    onGround: Boolean = false,
) : Packet<ClientboundTeleportEntityPacket>(ClientboundTeleportEntityPacket.STREAM_CODEC.build {
    writeVarInt(id)
    writeVector3d(location)
    writeByte(floor(pitch * 256.0f / 360.0f).toInt())
    writeByte(floor(yaw * 256.0f / 360.0f).toInt())
    writeBoolean(onGround)
})