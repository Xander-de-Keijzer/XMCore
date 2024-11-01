package nl.xandermarc.mc.lib.packets

import net.minecraft.network.protocol.game.ClientboundBundlePacket
import org.bukkit.entity.Player

class BundlePacket(
    private val packets: List<Packet<*>>
) : Packet<ClientboundBundlePacket>(
    ClientboundBundlePacket(packets.map { it.packet })
) {
    constructor(vararg packets: Packet<*>) : this(packets.toList())

    override fun send(player: Player) {
        if (packets.isNotEmpty()) { super.send(player) }
    }

    override fun send(players: Iterable<Player>) {
        if (packets.isNotEmpty()) { super.send(players) }
    }
}
