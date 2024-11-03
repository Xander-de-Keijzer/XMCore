package nl.xandermarc.mc.core

import io.netty.channel.Channel
import nl.xandermarc.mc.XMCProtocol
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.channel
import nl.xandermarc.mc.lib.extensions.deserialize
import nl.xandermarc.mc.lib.extensions.has
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

object EventListener: Listener {

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(Globals.QUIT_MESSAGE.deserialize(event.player.name))
        onConnection(event)
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Globals.JOIN_MESSAGE.deserialize(event.player.name))
        onConnection(event)
    }

    private fun onConnection(event: PlayerEvent) {
        event.player.inventory.removeAll { it.has(Keys.Item.TEMP) }
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent?) {
        if (XMCProtocol.isClosed()) return
        XMCProtocol.pendingNetworkManagers.forEach {
            XMCProtocol.getOrCreateHandler(it.channel)
        }
    }

    @EventHandler
    fun onPlayerLoginEvent(event: PlayerLoginEvent) {
        if (XMCProtocol.isClosed()) return
        XMCProtocol.playerCache[event.player.uniqueId] = event.player
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (XMCProtocol.isClosed()) return
        val channel: Channel = event.player.channel
        (channel.pipeline().get(XMCProtocol.IDENTIFIER) as? XMCProtocol.PacketHandler)?.apply {
            player = event.player
            XMCProtocol.playerCache.remove(event.player.uniqueId)
            return
        }
        XMCProtocol.getOrCreateHandler(channel).player = event.player
    }

    fun enable() {
        Globals.server.pluginManager.registerEvents(EventListener, Globals.instance)
    }


    fun disable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        AsyncPlayerPreLoginEvent.getHandlerList().unregister(this)
        PlayerLoginEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
    }

}
