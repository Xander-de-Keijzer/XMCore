package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.lib.math.Node
import nl.xandermarc.mc.lib.math.toVector3d
import nl.xandermarc.mc.rides.editor.Editor
import nl.xandermarc.mc.rides.editor.Tool
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
    override fun stop() {
        // TODO Stop editor
    }
}