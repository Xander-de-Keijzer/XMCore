package nl.xandermarc.mc.ride.tracked

import nl.xandermarc.mc.lib.extensions.toVector3d
import nl.xandermarc.mc.ride.editor.Editor
import nl.xandermarc.mc.ride.editor.Tool
import nl.xandermarc.mc.ride.tracked.track.Node
import nl.xandermarc.mc.ride.tracked.track.Track
import org.bukkit.Material
import org.bukkit.entity.Player

class TrackEditor(player: Player, private val track: Track) : Editor<TrackEditor>(
    player,
    mapOf(
        0 to Tool(
            Material.STICK,
            onRightClick = Tool.Event("New node") {
                this.track.addNode(Node(player.location.toVector3d()))
            }
        )
    )
) {
    override val instance = this
    override fun stop(): Unit =
        // TODO Stop editor
        Unit
}