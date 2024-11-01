package nl.xandermarc.mc.ride.tracked

import nl.xandermarc.mc.lib.editor.Editor
import nl.xandermarc.mc.lib.editor.Tool
import nl.xandermarc.mc.lib.extensions.toVector3d
import nl.xandermarc.mc.ride.tracked.track.Node
import nl.xandermarc.mc.ride.tracked.track.Track
import org.bukkit.Material
import org.bukkit.entity.Player

class TrackEditor(player: Player, private val track: Track) : Editor<TrackEditor>(player,
    mapOf(
        0 to Tool(Material.STICK,
            onRightClick = Tool.ToolEvent("New node") {
                track.addNode(Node(player.location.toVector3d()))
            }
        ),
        8 to Tool(Material.ANVIL,
            onRightClick = Tool.ToggleEvent()
        )
    ),
    mapOf(
        0 to Tool(Material.LIME_CONCRETE,
            onRightClick = Tool.ToolEvent("Move node up") { selected {
                location.y += 0.1
            } }
        )
    )
) {
    private var selected: Int = -1
    private fun selected(f: Node.() -> Unit) {
        track.nodes[selected]?.apply(f)
    }
    override val instance = this
    override fun stop(): Unit =
        // TODO Stop editor
        Unit
}