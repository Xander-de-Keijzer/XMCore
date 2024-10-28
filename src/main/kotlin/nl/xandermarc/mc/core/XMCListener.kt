package nl.xandermarc.mc.core

import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.deserialize
import nl.xandermarc.mc.lib.extensions.has
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object XMCListener: Listener {

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(XMC.QUIT_MESSAGE.deserialize(event.player.name))
        onConnection(event)
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(XMC.JOIN_MESSAGE.deserialize(event.player.name))
        onConnection(event)
    }

    private fun onConnection(event: PlayerEvent) {
        event.player.inventory.removeAll { it.has(Keys.Item.TEMP) }
    }

}
