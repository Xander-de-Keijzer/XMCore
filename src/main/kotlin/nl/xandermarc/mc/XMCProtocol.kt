package nl.xandermarc.mc

import io.ktor.util.collections.*
import io.netty.channel.*
import io.papermc.paper.network.ChannelInitializeListenerHolder
import net.minecraft.network.protocol.Packet
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.channel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

object XMCProtocol : Listener {
    private const val HANDLER_NAME = "xmc_protocol"
    private val insertedChannels = ConcurrentSet<Channel>()

    /*
        Protocol state
     */

    fun enable(plugin: JavaPlugin): XMCProtocol {
        ChannelInitializeListenerHolder.addListener(Keys.PROTOCOL) { insertHandler(it) }
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.onlinePlayers.forEach(::reset)
        return this
    }

    fun disable(): XMCProtocol {
        insertedChannels.forEach(::removeHandler)
        insertedChannels.clear()
        return this
    }

    /*
        Channel insertion
     */

    private fun insertHandler(channel: Channel) = PacketHandler().apply {
        channel.pipeline().addBefore("packet_handler", HANDLER_NAME, this)
        insertedChannels.add(channel)
    }

    private fun removeHandler(channel: Channel) {
        try { channel.pipeline().remove(HANDLER_NAME) }
        catch (ignored: Exception) {}
    }

    /*
        Utilities
     */

    private fun reset(player: Player) {
        player.channel?.apply {
            removeHandler(this)
            insertHandler(this).link(player)
        }
    }

    private fun ChannelHandler.link(player: Player) = apply {
        (this as? PacketHandler)?.player = player
    }

    /*
        Other
     */

    private fun onPacketReceive(player: Player, packet: Packet<*>) {
        //Globals.logger.info("$player $packet")
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.channel?.apply {
            pipeline().get(HANDLER_NAME).link(event.player)
        }
    }

    class PacketHandler : ChannelDuplexHandler() {
        @Volatile
        var player: Player? = null
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            if (msg is Packet<*> && player != null) {
                onPacketReceive(player!!, msg)
            }
            super.channelRead(ctx, msg)
        }

        override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
            super.write(ctx, msg, promise)
        }

    }

}
