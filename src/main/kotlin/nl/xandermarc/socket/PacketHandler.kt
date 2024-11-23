package nl.xandermarc.socket

import java.util.concurrent.ConcurrentHashMap

object PacketHandler {
    val listeners = ConcurrentHashMap<Long, (Packet.Data) -> Unit>()
    fun sendPacket(data: Packet.Data) {}
    fun receivePacket(data: Packet.Data) {
        if (data.action == Packet.Action.RESPONSE) {
            if (listeners.containsKey(data.uid)) {
                listeners[data.uid]?.invoke(data)
            } else {
                error("Received response ${data.uid} but no listener was registered.")
            }
        }
    }
}
