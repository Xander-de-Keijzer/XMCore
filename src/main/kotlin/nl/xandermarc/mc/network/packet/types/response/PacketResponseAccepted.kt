package nl.xandermarc.mc.network.packet.types.response

data class PacketResponseAccepted(
    val data: List<Any>
) : PacketResponseType(ORDINAL) { companion object { const val ORDINAL: Int = 2 } }
