package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import nl.xandermarc.mc.lib.extensions.build
import nl.xandermarc.mc.lib.extensions.writeVector3d
import nl.xandermarc.mc.lib.packets.AbstractPacket
import org.joml.Vector3d
import kotlin.math.floor

class TeleportEntityPacket(
    id: Int,
    location: Vector3d,
    yaw: Float = 0f,
    pitch: Float = 0f,
    onGround: Boolean = false,
) : AbstractPacket<ClientboundTeleportEntityPacket>(ClientboundTeleportEntityPacket.STREAM_CODEC.build {
    writeVarInt(id)
    writeVector3d(location)
    writeByte(floor(pitch * 256.0f / 360.0f).toInt())
    writeByte(floor(yaw * 256.0f / 360.0f).toInt())
    writeBoolean(onGround)
})