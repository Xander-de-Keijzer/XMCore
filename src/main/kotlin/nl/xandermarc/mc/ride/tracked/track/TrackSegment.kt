package nl.xandermarc.mc.ride.tracked.track

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.xandermarc.mc.lib.path.NodePath
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import nl.xandermarc.mc.ride.tracked.listeners.CartListener
import nl.xandermarc.mc.ride.tracked.listeners.TrainListener
import org.joml.Vector3d

@Serializable
open class TrackSegment(
    val path: NodePath,
    val aConnected: Int? = null,
    val bConnected: Int? = null,
    val frames: MutableList<Frame> = mutableListOf(),
) {
    @Transient
    private var trainListener = TrainListener {}
    private var cartListener = CartListener {}
    var resolution: Int = 0
    var length: Double = path.length()

    fun onTrainTick(listener: TrainListener) { trainListener = listener }
    fun onCartTick(listener: CartListener) { cartListener = listener }

    constructor(nodeA: Node, nodeB: Node) : this(NodePath(nodeA, nodeB))

    fun render(steps: Int) {
        require(steps > 0) { "Can't render less than 1 step." }
        if (steps < resolution) return

        var cumDist = 0.0
        val a = path.tangentAt(0.0).normalize()
        val b = path.secondDerivativeAt(0.0).add(a).normalize()
        val r = b.cross(a, Vector3d()).normalize()
        frames.clear()
        frames.add(Frame(0.0, 0.0, path.positionAt(0.0), a, r, r.cross(a, Vector3d())))

        for (t in 1 until steps) {
            val x0 = frames.last()
            val t1 = t.toDouble()/steps
            val x1o = path.positionAt(t1)
            val x1t = path.tangentAt(t1)
            val v1 = x1o.sub(x0.origin, Vector3d())
            val c1 = 2 / v1.dot(v1)
            val riL = x0.axis.sub(v1, Vector3d()).mul(c1).mul(v1.dot(x0.axis))
            val tiL = x0.tangent.sub(v1, Vector3d()).mul(c1).mul(v1.dot(x0.tangent))
            val v2 = x1t.sub(tiL)
            val x1a = riL.sub(v2, Vector3d()).mul(2/v2.dot(v2)).mul(v2.dot(riL))
            val x1n = x1a.cross(x1t, Vector3d())

            cumDist += x0.origin.distance(x1o)
            frames.add(Frame(t1, cumDist, x1o, x1t, x1a, x1n))
        }
        length = cumDist + frames.last().origin.distance(path.positionAt(1.0))
        resolution = steps
    }

    @Serializable
    data class Frame(
        val t: Double,
        val distance: Double,
        @Serializable(with = Vector3dSerializer::class)
        val origin: Vector3d,
        @Serializable(with = Vector3dSerializer::class)
        val tangent: Vector3d,
        @Serializable(with = Vector3dSerializer::class)
        val axis: Vector3d,
        @Serializable(with = Vector3dSerializer::class)
        val normal: Vector3d
    )
}
