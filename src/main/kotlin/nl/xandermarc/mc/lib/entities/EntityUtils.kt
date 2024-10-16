package nl.xandermarc.mc.lib.entities

import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Vector3d


fun Packet<in ClientGamePacketListener>.sendPacket(player: Player) {
    player.sendPacket(this)
}

fun Player.sendPacket(packet: Packet<in ClientGamePacketListener>) {
    (this as CraftPlayer).handle.connection.sendPacket(packet)
}

fun ItemStack.nms(): net.minecraft.world.item.ItemStack = (this as CraftItemStack).handle

fun <V : Packet<ClientGamePacketListener>> StreamCodec<FriendlyByteBuf, V>.build(builder: FriendlyByteBuf.() -> Unit): V {
    return decode(FriendlyByteBuf(Unpooled.buffer()).apply(builder))
}

fun <V : Packet<ClientGamePacketListener>> StreamCodec<RegistryFriendlyByteBuf, V>.build(
    registryAccess: RegistryAccess = RegistryAccess.EMPTY,
    builder: RegistryFriendlyByteBuf.() -> Unit
): V {
    return decode(RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess).apply(builder))
}

fun RegistryFriendlyByteBuf.writeEntityType(entityType: EntityType<*>) =
    ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(this, EntityType.ITEM_DISPLAY)

fun FriendlyByteBuf.writeVector3d(vector3d: Vector3d) =
    writeDouble(vector3d.x).writeDouble(vector3d.y).writeDouble(vector3d.z)

fun <T : Any> EntityDataAccessor<T>.create(value: T): SynchedEntityData.DataValue<T> =
    SynchedEntityData.DataValue.create(this, value)
