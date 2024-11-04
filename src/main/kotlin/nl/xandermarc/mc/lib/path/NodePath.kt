package nl.xandermarc.mc.lib.path

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.extensions.*
import nl.xandermarc.mc.ride.tracked.track.Node
import org.joml.Vector3d

@Serializable
data class NodePath(
    val nodeA: Node,
    val nodeB: Node,
) : Path() {
    private val p0 get() = nodeA.location
    private val p1 get() = nodeA.location
    private val p2 get() = nodeB.location
    private val p3 get() = nodeB.location

    override fun getPositionAt(t: Double): Vector3d = evaluate(p0, p1, p2, p3, t)
    override fun getTangentAt(t: Double): Vector3d = derivative(p0, p1, p2, p3, t).normalize()
    override fun length(): Double = p0.distance(p1) + p1.distance(p2) + p2.distance(p3)
    fun secondDerivativeAt(t: Double): Vector3d = secondDerivative(p0, p1, p2, p3, t)
}
