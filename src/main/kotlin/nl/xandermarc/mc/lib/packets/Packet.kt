package nl.xandermarc.mc.lib.packets

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import nl.xandermarc.mc.lib.extensions.sendPacket
import org.bukkit.entity.Player

abstract class Packet<T : Packet<ClientGamePacketListener>>(
    val packet: T
) {
    open fun send(player: Player) =
        player.sendPacket(packet)

    open fun send(players: Iterable<Player>) =
        players.forEach { it.sendPacket(packet) }
}
