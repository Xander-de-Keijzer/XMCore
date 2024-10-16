package nl.xandermarc.mc.lib.entities.packets

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener

abstract class Packet<T : Packet<ClientGamePacketListener>>(
    val packet: T
)
