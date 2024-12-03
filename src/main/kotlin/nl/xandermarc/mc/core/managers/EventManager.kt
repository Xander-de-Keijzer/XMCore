package nl.xandermarc.mc.core.managers

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.data.Config
import nl.xandermarc.mc.lib.data.EntityData
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.*
import nl.xandermarc.mc.lib.interfaces.Manager
import nl.xandermarc.mc.lib.packets.BundlePacket
import nl.xandermarc.mc.lib.packets.entities.AddEntityPacket
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.joml.Vector3f

object EventManager: Listener, Manager {

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(Config.Messages.QUIT.component(event.player.name))
        removeTempItems(event.player)
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Config.Messages.JOIN.component(event.player.name))
        BundlePacket(
            AddEntityPacket(24531, EntityType.ITEM_DISPLAY, event.player.location.toVector3d()),
            SetEntityDataPacket(24531,
                EntityData.ITEM_STACK_ID(item(Material.GRAY_CONCRETE).handle),
                EntityData.SCALE_ID(Vector3f(0.2f, 1.2f, 0.8f)),
                EntityData.SHARED_FLAGS_ID(0x40) // Only glowing
            )
        ).send(event.player)

//        if (ProtocolManager.isClosed()) return
//        val channel: Channel = event.player.channel
//        (channel.pipeline().get(ProtocolManager.IDENTIFIER) as? ProtocolManager.PacketHandler)?.apply {
//            player = event.player
//            ProtocolManager.playerCache.remove(event.player.uniqueId)
//            return
//        }
//        ProtocolManager.getOrCreateHandler(channel).player = event.player
    }

//    @EventHandler
//    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent?) {
//        if (ProtocolManager.isClosed()) return
//        ProtocolManager.pendingNetworkManagers.forEach {
//            ProtocolManager.getOrCreateHandler(it.channel)
//        }
//    }

//    @EventHandler
//    fun onPlayerLoginEvent(event: PlayerLoginEvent) {
//        if (ProtocolManager.isClosed()) return
//        ProtocolManager.playerCache[event.player.uniqueId] = event.player
//    }

    override fun enable(plugin: JavaPlugin): Manager {
        plugin.server.pluginManager.registerEvents(EventManager, plugin)
        plugin.server.onlinePlayers.forEach { player -> removeTempItems(player) }
        return this
    }

    override fun disable(): Manager {
        Globals.players.forEach { player -> removeTempItems(player) }
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        AsyncPlayerPreLoginEvent.getHandlerList().unregister(this)
        PlayerLoginEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        return this
    }

    private fun removeTempItems(player: Player) {
        player.inventory.removeAll { item ->
            item?.has(Keys.Item.TEMP) ?: false
        }
    }

}
