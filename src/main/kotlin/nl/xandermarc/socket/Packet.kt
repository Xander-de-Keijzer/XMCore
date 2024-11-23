package nl.xandermarc.socket

import kotlin.random.Random

sealed class Packet(
    val type: Type
) {
    enum class Domain { CONNECTION, USER, AUDIO_SESSION }
    enum class Connector { MINECRAFT, AUDIO_SERVER }
    enum class Action { POST, REQUEST, RESPONSE, EXCEPTION }
    enum class Type(val id: Int, val domain: Domain) {
        CREATE_AUDIO_SESSION(1, Domain.AUDIO_SESSION),
        ;

        companion object {
            fun of(id: Int) = entries.first { it.id == id }
            fun ofNull(id: Int) = entries.firstOrNull { it.id == id }
        }
    }
    data class Data(
        val type: Type,
        val sender: Connector,
        val receiver: Connector,
        val action: Action,
        val data: Map<String, Any?>,
        val uid: Long
    ) {
        fun send() {
            PacketHandler.sendPacket(this)
        }
        fun request(response: (Data) -> Unit) {
            PacketHandler.listeners[uid] = response
            send()
        }
    }
    fun build(
        sender: Connector,
        receiver: Connector,
        action: Action,
        data: Map<String, Any?>,
        uid: Long = Random.nextLong()
    ) = Data(
        type,
        sender,
        receiver,
        action,
        data,
        uid
    )
}
