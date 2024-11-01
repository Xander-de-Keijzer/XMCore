package nl.xandermarc.mc.network.packet.socket.post

import nl.xandermarc.mc.network.packet.Packet
import nl.xandermarc.mc.network.packet.PacketDirection
import nl.xandermarc.mc.network.packet.PacketDomain
import nl.xandermarc.mc.network.packet.types.PacketPost

class PacketStartConnection(
    password: String
) : Packet(
    PacketDirection.MINECRAFT_TO_WEBSOCKET,
    PacketDomain.SOCKET,
    PacketPost(listOf(password)),
    0
)
