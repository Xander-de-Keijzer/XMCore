package nl.xandermarc.test.network.packet.socket.post

import nl.xandermarc.test.network.packet.Packet
import nl.xandermarc.test.network.packet.PacketDirection
import nl.xandermarc.test.network.packet.PacketDomain
import nl.xandermarc.test.network.packet.types.PacketPost

class PacketStartConnection(
    password: String
) : Packet(
    PacketDirection.MINECRAFT_TO_WEBSOCKET,
    PacketDomain.SOCKET,
    PacketPost(listOf(password)),
    0
)
