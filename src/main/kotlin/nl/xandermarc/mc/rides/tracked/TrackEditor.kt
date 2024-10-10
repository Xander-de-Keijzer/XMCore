package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.rides.editor.Editor
import org.bukkit.entity.Player

class TrackEditor(player: Player, track: String): Editor<TrackEditor>(
    player,

) {
    override val instance = this
    private val track = TrackManager.get(track) ?: {
        player.sendPlainMessage("Track $track does not exist.") // TODO Prettier message
        close()
    }
    override fun stop() {
        // TODO Stop editor
    }
}