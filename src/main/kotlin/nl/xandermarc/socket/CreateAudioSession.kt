package nl.xandermarc.socket

import org.bukkit.entity.Player
import java.util.*

class CreateAudioSession(val player: Player) : Packet(Type.CREATE_AUDIO_SESSION) {
    fun request(response: (UUID) -> Unit) {
        build(
            Connector.MINECRAFT,
            Connector.AUDIO_SERVER,
            Action.REQUEST,
            mapOf("user_uuid" to player.uniqueId)
        ).request {
            response(it.data["session_uuid"] as UUID)
        }
    }
}
