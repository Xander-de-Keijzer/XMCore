package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.AbstractPacket
import nl.xandermarc.mc.lib.packets.build
import nl.xandermarc.mc.lib.packets.writeEntityType
import nl.xandermarc.mc.lib.packets.writeVector3d
import org.joml.Vector3d
import java.lang.Math.clamp
import java.util.*
import kotlin.math.floor

class AddEntityPacket(
    id: Int,
    entityType: EntityType<*>,
    location: Vector3d,
    uuid: UUID = UUID.randomUUID(),
    yaw: Float = 0f,
    pitch: Float = 0f,
    headYaw: Double = 0.0,
    entityData: Int = 0,
    velocity: Vector3d = Vector3d()
) : AbstractPacket<ClientboundAddEntityPacket>(ClientboundAddEntityPacket.STREAM_CODEC.build {
    writeVarInt(id)
    writeUUID(uuid)
    writeEntityType(entityType)
    writeVector3d(location)
    writeByte(floor(pitch * 256.0f / 360.0f).toInt())
    writeByte(floor(yaw * 256.0f / 360.0f).toInt())
    writeByte(floor(headYaw).toInt())
    writeVarInt(entityData)
    writeShort((clamp(velocity.x, -3.9, 3.9) * 8000.0).toInt())
    writeShort((clamp(velocity.y, -3.9, 3.9) * 8000.0).toInt())
    writeShort((clamp(velocity.z, -3.9, 3.9) * 8000.0).toInt())
})