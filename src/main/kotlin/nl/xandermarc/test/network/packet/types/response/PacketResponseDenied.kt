package nl.xandermarc.test.network.packet.types.response

data class PacketResponseDenied(
    val message: String,
) : PacketResponseType(ORDINAL) { companion object { const val ORDINAL: Int = 3 } }
