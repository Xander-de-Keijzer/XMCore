package nl.xandermarc.mc

import io.ktor.util.collections.*
import io.netty.channel.*
import io.papermc.paper.adventure.PaperAdventure
import io.papermc.paper.network.ChannelInitializeListenerHolder
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.extensions.channel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object XMCProtocol : Listener {
    private const val HANDLER_NAME = "xmc_protocol"
    private val insertedChannels = ConcurrentSet<Channel>()

    /*
        Protocol state
     */

    fun enable(plugin: JavaPlugin) {
        ChannelInitializeListenerHolder.addListener(Keys.PROTOCOL) { insertHandler(it) }
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.onlinePlayers.forEach(::reset)
    }

    fun disable() {
        insertedChannels.forEach(::removeHandler)
        insertedChannels.clear()
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
            write(ClientboundResourcePackPushPacket(
                UUID.randomUUID(),
                "https://cdn.modrinth.com/data/w0TnApzs/versions/UIpuBfGX/Faithful%2032x%20-%201.21.3.zip",
                "test",
                true,
                Optional.of(PaperAdventure.asVanilla(Globals.message.deserialize("<red>ResourcePack is required")))
            ))
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
