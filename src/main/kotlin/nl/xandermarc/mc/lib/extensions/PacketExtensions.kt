@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import net.minecraft.world.entity.EntityType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Vector3d


val Player.handle: ServerPlayer
    get() = (this as CraftPlayer).handle

val Player.connection: ServerPlayerConnection
    get() = handle.connection

val Player.network: Connection
    get() = handle.connection.connection

val Player.channel: Channel
    get() = network.channel

fun Player.sendPacket(packet: Packet<in ClientGamePacketListener>) =
    connection.send(packet)

fun Player.sendPacket(packet: nl.xandermarc.mc.lib.packets.Packet<*>) =
    connection.send(packet.packet)

val ItemStack.handle: net.minecraft.world.item.ItemStack
    get() = (this as CraftItemStack).handle

inline fun <V : Packet<ClientGamePacketListener>> StreamCodec<FriendlyByteBuf, V>.build(
    builder: FriendlyByteBuf.() -> Unit
): V =
    decode(FriendlyByteBuf(Unpooled.buffer()).apply(builder))

inline fun <V : Packet<ClientGamePacketListener>> StreamCodec<RegistryFriendlyByteBuf, V>.build(
    registryAccess: RegistryAccess = RegistryAccess.EMPTY,
    builder: RegistryFriendlyByteBuf.() -> Unit
): V =
    decode(RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess).apply(builder))

fun RegistryFriendlyByteBuf.writeEntityType(entityType: EntityType<*>) =
    ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(this, entityType)

fun FriendlyByteBuf.writeVector3d(vector3d: Vector3d): FriendlyByteBuf =
    writeDouble(vector3d.x).writeDouble(vector3d.y).writeDouble(vector3d.z)

fun <T : Any> EntityDataAccessor<T>.create(value: T): SynchedEntityData.DataValue<T> =
    SynchedEntityData.DataValue.create(this, value)
