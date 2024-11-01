package nl.xandermarc.test.network.packet.types

import nl.xandermarc.test.network.packet.types.response.PacketResponseType

data class PacketResponse(
    val id: Int,
    val response: PacketResponseType
) : PacketType(ORDINAL) { companion object { const val ORDINAL: Int = 3 } }
