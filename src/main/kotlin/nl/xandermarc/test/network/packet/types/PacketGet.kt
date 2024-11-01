package nl.xandermarc.test.network.packet.types

data class PacketGet(
    val data: List<Any>
) : PacketType(ORDINAL) { companion object { const val ORDINAL: Int = 2 } }
