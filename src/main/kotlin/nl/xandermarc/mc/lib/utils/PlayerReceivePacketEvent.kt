package nl.xandermarc.mc.lib.utils

import net.minecraft.network.protocol.Packet
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerReceivePacketEvent(var packet: Packet<*>, player: Player): PlayerEvent(player), Cancellable {
    private var cancelled = false
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
    override fun getHandlers() = handlerList
    companion object { val handlerList = HandlerList() }
}
