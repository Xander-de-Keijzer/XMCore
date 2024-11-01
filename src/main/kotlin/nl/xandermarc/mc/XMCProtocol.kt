package nl.xandermarc.mc

import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.server.MinecraftServer
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.channel
import nl.xandermarc.mc.lib.utils.PlayerReceivePacketEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.Volatile

object XMCProtocol {
    private const val IDENTIFIER: String = "XMCProtocol"
    private val serverConnection = MinecraftServer.getServer().connection
    private val closed = AtomicBoolean(false)
    private val playerCache: MutableMap<UUID, Player> = Collections.synchronizedMap(HashMap())
    private val injectedChannels: MutableSet<Channel> = Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap()))
    private val logger = Logger.getLogger(XMCProtocol::class.simpleName)

    fun isClosed(): Boolean = closed.get()

    fun enable() {
        logger.level = Level.SEVERE
        Globals.server.pluginManager.registerEvents(EventListener, Globals.instance)
        Globals.server.onlinePlayers.forEach { getOrCreateHandler(it.channel).player = it }
    }

    fun disable() {
        if (closed.getAndSet(true)) return
        EventListener.unregister()
        synchronized(serverConnection) {
            injectedChannels.forEach {
                try {
                    it.eventLoop().submit { it.pipeline().remove(IDENTIFIER) }
                } catch (exception: Exception) {
                    logger.log(Level.SEVERE, "[XMCProtocol] Error while trying to uninject player", exception)
                }
            }
        }
        playerCache.clear()
        injectedChannels.clear()
    }

    private fun onPacketReceiveAsync(player: Player, packet: Packet<*>): Packet<*>? {
        if (!Globals.PACKET_EVENT_ENABLED) return packet
        return PlayerReceivePacketEvent(packet, player).run {
            if (callEvent()) this.packet else null
        }
    }

    private fun getOrCreateHandler(channel: Channel): PacketHandler {
        var handler = PacketHandler()
        channel.eventLoop().submit {
            if (isClosed()) return@submit
            if (injectedChannels.add(channel)) {
                try { channel.pipeline().addBefore("packet_handler", IDENTIFIER, handler) }
                catch (ignored: IllegalArgumentException) { handler = channel.pipeline().get(IDENTIFIER) as PacketHandler
                }
            }
        }
        return handler
    }

    private fun getPendingNetworkManagers(): Queue<Connection?>? {
        return try {
            val pendingField = serverConnection::class.java.getDeclaredField("pending").apply { isAccessible = true }
            @Suppress("UNCHECKED_CAST")
            pendingField.get(serverConnection) as Queue<Connection?>
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "An exception occurred during retrieval of pending server connections.", e)
            return null
        }
    }

    private object EventListener : Listener {
        @EventHandler
        fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent?) {
            if (isClosed()) return
            synchronized(serverConnection) {
                getPendingNetworkManagers()?.let { pending ->
                    synchronized(pending) {
                        pending.filterNotNull().forEach { getOrCreateHandler(it.channel) }
                    }
                }
            }
        }

        @EventHandler
        fun onPlayerLoginEvent(event: PlayerLoginEvent) {
            if (isClosed()) return
            playerCache[event.player.uniqueId] = event.player
        }

        @EventHandler
        fun onPlayerJoinEvent(event: PlayerJoinEvent) {
            if (isClosed()) return
            val player = event.player
            val channel: Channel = player.channel
            val channelHandler = channel.pipeline().get(IDENTIFIER)
            if (channelHandler is PacketHandler) {
                channelHandler.player = player
                playerCache.remove(player.uniqueId)
            } else {
                getOrCreateHandler(channel).player = player
            }
        }

        fun unregister() {
            AsyncPlayerPreLoginEvent.getHandlerList().unregister(this)
            PlayerLoginEvent.getHandlerList().unregister(this)
            PlayerJoinEvent.getHandlerList().unregister(this)
        }
    }

    private class PacketHandler : ChannelDuplexHandler() {
        @Volatile
        var player: Player? = null

        override fun channelUnregistered(ctx: ChannelHandlerContext) {
            injectedChannels.remove(ctx.channel())
            super.channelUnregistered(ctx)
        }

        override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
            val player = player
            if (player == null) {
                super.channelRead(ctx, packet)
                return
            }
            val newPacket = onPacketReceiveAsync(player, packet as Packet<*>) ?: return
            super.channelRead(ctx, newPacket)
        }
    }
}
