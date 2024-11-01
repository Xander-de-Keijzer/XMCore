package nl.xandermarc.mc.network.packet.session.get

import nl.xandermarc.mc.network.packet.Packet
import nl.xandermarc.mc.network.packet.PacketDirection
import nl.xandermarc.mc.network.packet.PacketDomain
import nl.xandermarc.mc.network.packet.types.PacketGet
import java.util.*

class PacketCreateSession(
    playerUUID: UUID,
) : Packet(
    PacketDirection.MINECRAFT_TO_WEBSOCKET,
    PacketDomain.SESSION,
    PacketGet(listOf(playerUUID)),
    0
)
