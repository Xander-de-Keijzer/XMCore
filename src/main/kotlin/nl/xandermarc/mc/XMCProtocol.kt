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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.Volatile
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object XMCProtocol {
    const val IDENTIFIER: String = "XMCProtocol"
    private val serverConnection = MinecraftServer.getServer().connection
    private val closed = AtomicBoolean(false)
    val playerCache: MutableMap<UUID, Player> = Collections.synchronizedMap(HashMap())
    private val injectedChannels: MutableSet<Channel> = Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap()))
    private val logger = Logger.getLogger(XMCProtocol::class.simpleName)

    fun isClosed(): Boolean = closed.get()

    fun enable() {
        Globals.server.onlinePlayers.forEach { getOrCreateHandler(it.channel).player = it }
    }

    fun disable() {
        if (closed.getAndSet(true)) return
        synchronized(serverConnection) {
            injectedChannels.forEach {
                try {
                    it.eventLoop().submit { it.pipeline().remove(IDENTIFIER) }
                } catch (ignored: NoSuchElementException) {}
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

    fun getOrCreateHandler(channel: Channel): PacketHandler {
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

    @Suppress("UNCHECKED_CAST")
    val pendingNetworkManagers by lazy {
        serverConnection::class.declaredMemberProperties
            .first { it.name == "pending" }
            .run {
                isAccessible = true
                call(serverConnection) as Queue<Connection?>
            }
            .filterNotNull()
    }

    class PacketHandler : ChannelDuplexHandler() {
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
