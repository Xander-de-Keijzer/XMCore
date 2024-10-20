package nl.xandermarc.mc.lib.path

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.extensions.cubed
import nl.xandermarc.mc.lib.extensions.squared
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Vector3d

@Serializable
data class BezierPath(
    @Serializable(with = Vector3dSerializer::class)
    val p0: Vector3d = Vector3d(),  // Starting point
    @Serializable(with = Vector3dSerializer::class)
    val p1: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    val p2: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    val p3: Vector3d = Vector3d(),
) : Path() {
    override fun getPositionAt(t: Double): Vector3d  = evaluate(p0, p1, p2, p3, t)
    override fun getForwardAt(t: Double): Vector3d = derivative(p0, p1, p2, p3, t).normalize()
    override fun length(): Double = p0.distance(p1) + p1.distance(p2) + p2.distance(p3)

    private fun evaluate(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
        val u = 1.0 - t
        val term0 = p0.mul(u.cubed(), Vector3d())                        // (1 - t)^3 * P0
        val term1 = p1.mul(3.0 * u.squared() * t, Vector3d())     // 3 * (1 - t)^2 * t * P1
        val term2 = p2.mul(3.0 * u * t.squared(), Vector3d())     // 3 * (1 - t) * t^2 * P2
        val term3 = p3.mul(t.cubed(), Vector3d())                       // t^3 * P3

        return term0.add(term1).add(term2).add(term3)
    }

    private fun derivative(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
        val u = 1.0 - t
        val term0 = p1.sub(p0, Vector3d()).mul(3.0 * u.squared())        // 3 * (1 - t)^2 * (P1 - P0)
        val term1 = p2.sub(p1, Vector3d()).mul(6.0 * u * t)     // 6 * (1 - t) * t * (P2 - P1)
        val term2 = p3.sub(p2, Vector3d()).mul(3.0 * t.squared())        // 3 * t^2 * (P3 - P2)

        return term0.add(term1).add(term2)
    }
}
