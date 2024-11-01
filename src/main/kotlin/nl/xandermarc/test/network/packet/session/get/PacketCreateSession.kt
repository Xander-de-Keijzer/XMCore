package nl.xandermarc.test.network.packet.session.get

import nl.xandermarc.test.network.packet.Packet
import nl.xandermarc.test.network.packet.PacketDirection
import nl.xandermarc.test.network.packet.PacketDomain
import nl.xandermarc.test.network.packet.types.PacketGet
import java.util.*

class PacketCreateSession(
    playerUUID: UUID,
) : Packet(
    PacketDirection.MINECRAFT_TO_WEBSOCKET,
    PacketDomain.SESSION,
    PacketGet(listOf(playerUUID)),
    0
)
