package nl.xandermarc.test.network.packet

import nl.xandermarc.mc.lib.extensions.warn
import nl.xandermarc.test.network.packet.types.*
import nl.xandermarc.test.network.packet.types.response.PacketResponseAccepted
import nl.xandermarc.test.network.packet.types.response.PacketResponseAcknowledged
import java.util.concurrent.atomic.AtomicInteger

abstract class Packet(
    val direction: PacketDirection,
    val domain: PacketDomain,
    val type: PacketType,
    val ordinal: Int,
    val id: Int = nextKey,
) {
    companion object {
        private val generator = AtomicInteger(0)
        val nextKey: Int get() = generator.getAndIncrement()
    }
    fun send() {}
    fun send(block: (List<Any>) -> Unit) {
        val listenerKey = try { responseKey }
        catch (e: IllegalStateException) { warn(e.message ?: "Failed to create a response key to listen to"); return }

        nl.xandermarc.test.network.NetworkManager.onResponse(listenerKey, block)
    }
    val key = listOf(
        direction.ordinal,
        domain.ordinal,
        type.ordinal,
        ordinal,
        type.responseOrdinal
    )
    private val responseKey get() = listOf(
        when(direction) {
            PacketDirection.MINECRAFT_TO_WEBSOCKET -> PacketDirection.WEBSOCKET_TO_MINECRAFT.ordinal
            PacketDirection.WEBSOCKET_TO_MINECRAFT -> PacketDirection.MINECRAFT_TO_WEBSOCKET.ordinal
        },
        domain.ordinal,
        PacketResponse.ORDINAL,
        ordinal,
        when(type) {
            is PacketException, is PacketResponse -> throw IllegalStateException("Can't create a response to a response")
            is PacketPost -> PacketResponseAcknowledged.ORDINAL
            is PacketGet -> PacketResponseAccepted.ORDINAL
        }
    )
}
