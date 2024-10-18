package nl.xandermarc.mc.lib.packets

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import nl.xandermarc.mc.lib.extensions.sendPacket
import org.bukkit.entity.Player

abstract class AbstractPacket<T : Packet<ClientGamePacketListener>>(
    val packet: T
) {
    fun sendPacket(player: Player) =
        player.sendPacket(packet)

    fun sendPacket(players: Iterable<Player>) =
        players.forEach { it.sendPacket(packet) }
}
