package nl.xandermarc.mc.core.protocol

import TinyProtocol
import io.netty.channel.Channel
import nl.xandermarc.mc.lib.XMC
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Listener(plugin: JavaPlugin): TinyProtocol(plugin) {

    override fun onPacketReceiveAsync(sender: Player?, channel: Channel, packet: Any): Any? {
        XMC.logger.info("RECV: $sender $channel $packet ${packet::class}")
        return packet
    }

    override fun onPacketSendAsync(receiver: Player?, channel: Channel, packet: Any): Any? {
        XMC.logger.info("SEND: $receiver $channel $packet ${packet::class}")
        return packet
    }
}
