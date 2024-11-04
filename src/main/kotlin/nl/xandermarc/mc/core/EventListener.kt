package nl.xandermarc.mc.core

import io.netty.channel.Channel
import nl.xandermarc.mc.ProtocolManager
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.channel
import nl.xandermarc.mc.lib.extensions.deserialize
import nl.xandermarc.mc.lib.extensions.has
import nl.xandermarc.mc.lib.utils.Manager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.plugin.java.JavaPlugin

object EventListener: Listener, Manager {

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
        if (ProtocolManager.isClosed()) return
        ProtocolManager.pendingNetworkManagers.forEach {
            ProtocolManager.getOrCreateHandler(it.channel)
        }
    }

    @EventHandler
    fun onPlayerLoginEvent(event: PlayerLoginEvent) {
        if (ProtocolManager.isClosed()) return
        ProtocolManager.playerCache[event.player.uniqueId] = event.player
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (ProtocolManager.isClosed()) return
        val channel: Channel = event.player.channel
        (channel.pipeline().get(ProtocolManager.IDENTIFIER) as? ProtocolManager.PacketHandler)?.apply {
            player = event.player
            ProtocolManager.playerCache.remove(event.player.uniqueId)
            return
        }
        ProtocolManager.getOrCreateHandler(channel).player = event.player
    }

    override fun enable(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(EventListener, plugin)
    }


    override fun disable() {
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        AsyncPlayerPreLoginEvent.getHandlerList().unregister(this)
        PlayerLoginEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
    }

}
