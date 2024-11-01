package nl.xandermarc.mc.network.packet.types

data class PacketException(
    val message: String
) : PacketType(ORDINAL) { companion object { const val ORDINAL: Int = 0 } }
