package nl.xandermarc.mc.core.managers

import io.netty.channel.Channel
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.channel
import nl.xandermarc.mc.lib.extensions.deserialize
import nl.xandermarc.mc.lib.extensions.has
import nl.xandermarc.mc.lib.utils.Manager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

object EventManager: Listener, Manager {

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(Globals.QUIT_MESSAGE.deserialize(event.player.name))
        removeTempItems(event.player)
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Globals.JOIN_MESSAGE.deserialize(event.player.name))
        removeTempItems(event.player)
        if (ProtocolManager.isClosed()) return
        val channel: Channel = event.player.channel
        (channel.pipeline().get(ProtocolManager.IDENTIFIER) as? ProtocolManager.PacketHandler)?.apply {
            player = event.player
            ProtocolManager.playerCache.remove(event.player.uniqueId)
            return
        }
        ProtocolManager.getOrCreateHandler(channel).player = event.player
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

    override fun enable(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(EventManager, plugin)
        plugin.server.onlinePlayers.forEach { player -> removeTempItems(player) }
    }

    override fun disable() {
        Globals.players.forEach { player -> removeTempItems(player) }
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        AsyncPlayerPreLoginEvent.getHandlerList().unregister(this)
        PlayerLoginEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
    }

    private fun removeTempItems(player: Player) {
        player.inventory.removeAll { item ->
            item.has(Keys.Item.TEMP)
        }
    }

}