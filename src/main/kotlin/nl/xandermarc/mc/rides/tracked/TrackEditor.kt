package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.Main
import nl.xandermarc.mc.lib.math.CubicBezier
import nl.xandermarc.mc.lib.math.Vec
import nl.xandermarc.mc.lib.math.extensions.toLocation
import nl.xandermarc.mc.lib.math.extensions.toRadians
import nl.xandermarc.mc.lib.math.extensions.toVec
import nl.xandermarc.mc.lib.math.extensions.toVector3f
import nl.xandermarc.mc.rides.editor.Editor
import nl.xandermarc.mc.rides.editor.Tool
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class TrackEditor(
    track: String,
    player: Player
) : Editor<TrackEditor>(
    player,
    mapOf(
        0 to Tool(
            onRightClick = "test" to {
                this.track.nodes.add(
                    Node(player.location.toVec())
                )
                updateVisualization()
            }
        )
    )
) {
    override val instance = this
    override fun stop() {
        player.sendMessage("stop")
        displays.forEach { it.remove() }
        displays.clear()
    }
    private val track = TrackManager.getOrCreate(track)
    private val displays = arrayListOf<ItemDisplay>()

    private fun updateVisualization() {
        displays.forEach { it.remove() }
        displays.clear()
        displays.addAll(
            track.nodes.map { node ->
                (Main.world.spawnEntity(node.location.toLocation(), EntityType.ITEM_DISPLAY) as ItemDisplay).apply {
                    setItemStack(ItemStack(Material.WHITE_CONCRETE))
                    transformation = Transformation(Vector3f(), Quaternionf(), Vector3f(0.25f, 0.05f, 0.15f), Quaternionf())
                }
            }
        )
        track.nodes.windowed(2, 1) { (node1, node2) ->
            val c1 = Vec(
                node1.radius * cos(node1.pitch.toRadians()) * sin(node1.yaw.toRadians()),
                node1.radius * sin(node1.pitch.toRadians()),
                node1.radius * cos(node1.pitch.toRadians()) * cos(node1.yaw.toRadians())
            )
            val p2 = node2.location - node1.location
            val c2 = Vec(
                node2.radius * cos(node2.pitch.toRadians()) * sin(node2.yaw.toRadians()),
                node2.radius * sin(node2.pitch.toRadians()),
                node2.radius * cos(node2.pitch.toRadians()) * cos(node2.yaw.toRadians())
            ) + p2
            val bezier = CubicBezier(c1, c2, p2)
            // Spawn ItemDisplay entities based on RMF frames
            val mBlocks = (bezier.arcLength(1000) * 100).toInt()

            displays.addAll(
                (0..(mBlocks/10)).map {
                    val d = it.toDouble() / 10
                    (Main.world.spawnEntity(bezier.pointAtDistance(d).plus(node1.location).toLocation(), EntityType.ITEM_DISPLAY) as ItemDisplay).apply {
                        setItemStack(ItemStack(Material.GRAY_CONCRETE))
                        transformation = Transformation(
                            Vector3f(),          // Position offset
                            bezier.rotationAt(d),            // Rotation based on RMF
                            Vector3f(0.15f, 0.04f, 0.10f), // Scale
                            Quaternionf()        // Additional rotation (if any)
                        )
                    }
                }
            )
        }
    }
}