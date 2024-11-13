package nl.xandermarc.mc.ride

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.xandermarc.mc.lib.AbstractEditor
import nl.xandermarc.mc.lib.extensions.toVector3d
import nl.xandermarc.mc.lib.extensions.vec3d
import nl.xandermarc.mc.lib.serializers.QuaterniondSerializer
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.joml.Quaterniond
import org.joml.Vector3d

@Serializable
data class Track(
    val name: String,
    val nodes: MutableMap<Int, Node> = mutableMapOf(),
    val segments: MutableMap<Int, Segment> = mutableMapOf(),
    var selectedNodeID: Int = 0
) {
    val nextNodeID get() = (nodes.maxOfOrNull { it.key } ?: 0) + 1

    @Serializable
    data class Node(
        val location: @Serializable(with=Vector3dSerializer::class) Vector3d,
        val rotation: @Serializable(with=Vector3dSerializer::class) Vector3d,
        var radius: Double,
        val connections: MutableList<Int> = mutableListOf()
    )
    @Serializable
    data class Point(
        val location: @Serializable(with=Vector3dSerializer::class) Vector3d,
        val rotation: @Serializable(with=QuaterniondSerializer::class) Quaterniond
    )
    @Serializable
    data class Segment(
        val points: List<Point>,
        var a: Segment? = null,
        var b: Segment? = null,
    ) {
        @Transient
        var controller: Ride.Controller? = null
            set(value) { field=value; value?.segment = this }
    }
    @Serializable
    data class Position(
        val segment: Segment,
        val position: Double,
        val direction: Boolean
    )

    class Editor internal constructor(
        track: Track,
        player: Player
    ): AbstractEditor<Track>(
        track,
        player,
        mapOf(
            0 to Tool("Test", Material.STONE,
                "add X" to {
                    val node = nodes[selectedNodeID]
                    if (node == null) player.sendMessage("No node selected")
                    else node.location.y += 1.0
                }
            ),
            1 to Tool("New", Material.TRIPWIRE_HOOK,
                "new" to {
                    selectedNodeID = nextNodeID
                    nodes[nextNodeID] = Node(
                        player.location.toVector3d(),
                        vec3d(),
                        3.0
                    )

                }
            )
        )
    )

    fun position(segment: Int, position: Double, direction: Boolean) = segments[segment]?.run { Position(this, position, direction) }
    fun position(segment: Segment, position: Double, direction: Boolean) = Position(segment, position, direction)
    fun startEditor(player: Player) = Editor(this, player).apply { open() }
}
