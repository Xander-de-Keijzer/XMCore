package nl.xandermarc.mc.rides.tracked

import kotlinx.coroutines.ensureActive
import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.lib.math.Node
import nl.xandermarc.mc.lib.math.path.BezierPath
import org.joml.Vector3d
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

@Serializable
data class Track(
    var name: String,
    val nodes: HashMap<Int, Node> = hashMapOf(),
    val segments: HashMap<Int, TrackSegment> = hashMapOf()
) {
    private var nextNodeId = (nodes.keys.maxOrNull() ?: 0) + 1

    fun addNode(node: Node): Int {
        nodes[nextNodeId++] = node
        return nextNodeId - 1
    }

    fun getSegment(id: Int?) = segments.getOrDefault(id, null)
    fun getSegmentId(segment: TrackSegment) = segments.filterValues { it == segment }.keys.firstOrNull()

    // TODO
    private fun canLoadSegments(): Boolean {
        return false
    }

    // TODO
    private suspend fun loadSegments() {
        debug("Loading segments of track $name")
        coroutineContext.ensureActive()
    }

    // TODO
    private suspend fun generateSegments() {
        debug("Generating segments of track $name")
        for (i in (1..10)) {
            coroutineContext.ensureActive()
            segments[i] = TrackSegment(BezierPath(
                Vector3d(Random.nextDouble(10.0), Random.nextDouble(10.0), Random.nextDouble(10.0)),
                Vector3d(Random.nextDouble(10.0), Random.nextDouble(10.0), Random.nextDouble(10.0)),
                Vector3d(Random.nextDouble(10.0), Random.nextDouble(10.0), Random.nextDouble(10.0)),
                Vector3d(Random.nextDouble(10.0), Random.nextDouble(10.0), Random.nextDouble(10.0))
            ).apply { generateTValues() })
            debug("Generated segment $i of track $name")
        }
    }

    suspend fun loadOrGenerateSegments() {
        coroutineContext.ensureActive()
        if (canLoadSegments()) loadSegments().info("Loaded ${segments.size} segments of track $name")
        else generateSegments().info("Generated ${segments.size} segments for track $name")
    }

    fun isEmpty() = nodes.isEmpty() && segments.isEmpty()
}
