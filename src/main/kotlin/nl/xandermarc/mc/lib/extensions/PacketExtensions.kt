@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.core.registries.Registries
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Vector3d
import java.util.*
import java.util.function.Function


val Player.handle: ServerPlayer
    get() = (this as CraftPlayer).handle

val Player.connection: ServerGamePacketListenerImpl?
    get() = handle.connection

val Player.network: Connection?
    get() = connection?.connection

val Player.channel: Channel?
    get() = network?.channel

val registryByteBufBuilder: Function<ByteBuf, RegistryFriendlyByteBuf> =
    RegistryFriendlyByteBuf.decorator(MinecraftServer.getServer().registryAccess())
fun registryByteBuf(buf: ByteBuf = Unpooled.buffer()) = registryByteBufBuilder.apply(buf)

fun Player.sendPacket(packet: Packet<in ClientGamePacketListener>) {
    val connection = this.connection ?: return Globals.logger.warn { "Trying to send a packet but no connection is present for ${this.name}." }
    connection.sendPacket(packet)
}

fun Player.sendPacket(packet: nl.xandermarc.mc.lib.packets.Packet<*>) = sendPacket(packet.packet)

val ItemStack.handle: net.minecraft.world.item.ItemStack
    get() = (this as CraftItemStack).handle

inline fun <reified V : Packet<ClientGamePacketListener>> StreamCodec<FriendlyByteBuf, V>.build(
    builder: FriendlyByteBuf.() -> Unit
): V = decode(FriendlyByteBuf(Unpooled.buffer()).apply(builder))

inline fun <reified V : Packet<ClientGamePacketListener>> StreamCodec<RegistryFriendlyByteBuf, V>.buildDefault(
    builder: RegistryFriendlyByteBuf.() -> Unit
): V = decode(registryByteBuf().apply(builder))

fun RegistryFriendlyByteBuf.writeEntityType(entityType: EntityType<*>) =
    ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(this, entityType)

fun FriendlyByteBuf.writeVector3d(vector3d: Vector3d): FriendlyByteBuf =
    writeDouble(vector3d.x).writeDouble(vector3d.y).writeDouble(vector3d.z)

fun <T : Any> EntityDataAccessor<T>.create(value: T): SynchedEntityData.DataValue<T> =
    SynchedEntityData.DataValue.create(this, value)

operator fun <T : Any> EntityDataAccessor<T>.invoke(value: T): SynchedEntityData.DataValue<T> = create(value)
fun <T : Any> EntityDataAccessor<Optional<T>>.optional(value: T): SynchedEntityData.DataValue<Optional<T>> = create(Optional.of(value))

fun EntityDataAccessor<Optional<Component>>.optional(value: net.kyori.adventure.text.Component): SynchedEntityData.DataValue<Optional<Component>> = create(Optional.of(PaperAdventure.asVanilla(value)))
operator fun EntityDataAccessor<Component>.invoke(value: net.kyori.adventure.text.Component): SynchedEntityData.DataValue<Component> = create(PaperAdventure.asVanilla(value))

fun <T : Any> RegistryFriendlyByteBuf.write(value: SynchedEntityData.DataValue<T>) = value.write(this)
