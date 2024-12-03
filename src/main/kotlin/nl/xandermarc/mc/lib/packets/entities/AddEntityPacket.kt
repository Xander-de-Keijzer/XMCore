package nl.xandermarc.mc.lib.packets.entities

import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.extensions.buildDefault
import nl.xandermarc.mc.lib.extensions.writeVector3d
import nl.xandermarc.mc.lib.packets.Packet
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
) : Packet<ClientboundAddEntityPacket>(ClientboundAddEntityPacket.STREAM_CODEC.buildDefault {
    writeVarInt(id)
    writeUUID(uuid)
    ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(
        this,
        entityType,
    )
    writeVector3d(location)
    writeByte(floor(pitch * 256.0f / 360.0f).toInt())
    writeByte(floor(yaw * 256.0f / 360.0f).toInt())
    writeByte(floor(headYaw).toInt())
    writeVarInt(entityData)
    writeShort((clamp(velocity.x, -3.9, 3.9) * 8000.0).toInt())
    writeShort((clamp(velocity.y, -3.9, 3.9) * 8000.0).toInt())
    writeShort((clamp(velocity.z, -3.9, 3.9) * 8000.0).toInt())
})
