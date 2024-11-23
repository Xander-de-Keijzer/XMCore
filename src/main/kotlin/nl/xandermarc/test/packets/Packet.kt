package nl.xandermarc.test.packets

import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.uuid.Uuid

enum class Packet(private val id: Int, private val domain: Domain) {
    CREATE_AUDIO_SESSION(1, Domain.AUDIO_SESSION)
    ;
    private val connector = Connector.MINECRAFT
    enum class Domain { CONNECTION, USER, AUDIO_SESSION }
    enum class Connector { MINECRAFT, AUDIO_SERVER }
    enum class Type { POST, REQUEST, RESPONSE, EXCEPTION }
    data class Data(
        val id: Int,
        val domain: Domain,
        val sender: Connector,
        val receiver: Connector,
        val type: Type,
        val data: Map<String, Any?>,
        val uid: Long,
    ) { val packet get() = Packet.entries.first { it.id == id } }
    sealed class Builder(val packet: Packet, val data: Map<String, Any?>, private val defaultReceiver: Connector) {
        fun send(receiver: Connector = defaultReceiver) =
            packet.send(receiver, data)
        fun request(receiver: Connector = defaultReceiver, response: (Data) -> Unit) =
            packet.request(receiver, data, response)
    }
    class CreateAudioSession(player: Player) :
        Builder(CREATE_AUDIO_SESSION, mapOf("player_uuid" to player.uniqueId), Connector.AUDIO_SERVER)
    { fun requestUUID(response: (UUID) -> Unit) = request(Connector.AUDIO_SERVER) {
        response(data["session_uuid"] as UUID)
    } }

    fun build(
        receiver: Connector,
        type: Type,
        data: Map<String, Any?>,
        sender: Connector = connector,
        uid: Long = Random.nextLong(),
    ) = Data(id, domain, sender, receiver, type, data, uid)

    fun send(receiver: Connector, data: Map<String, Any?>) {
        PacketConnection.sendPacket(build(receiver, Type.POST, data))
    }

    fun request(receiver: Connector, data: Map<String, Any?>, response: (Data) -> Unit) {
        build(receiver, Type.REQUEST, data).apply {
            PacketConnection.listeners[uid] = response
            PacketConnection.sendPacket(this)
        }
    }
}

object PacketConnection {
    val listeners = ConcurrentHashMap<Long, (Packet.Data) -> Unit>()
    fun sendPacket(data: Packet.Data) {}
    fun receivePacket(data: Packet.Data) {
        if (data.type == Packet.Type.RESPONSE) {
            if (listeners.containsKey(data.uid)) {
                listeners[data.uid]?.invoke(data)
            } else {
                error("Received response ${data.uid} but no listener was registered.")
            }
        }
        Packet.CreateAudioSession(Globals.players.first()).requestUUID {

        }
    }
}
