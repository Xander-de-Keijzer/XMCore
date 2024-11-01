package nl.xandermarc.test.network.packet.types

sealed class PacketType(val ordinal: Int) {
    val responseOrdinal get() = (this as? PacketResponse)?.response?.ordinal ?: -1
}
