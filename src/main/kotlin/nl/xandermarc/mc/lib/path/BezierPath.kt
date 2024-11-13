package nl.xandermarc.mc.lib.path

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.Math.derivative
import nl.xandermarc.mc.lib.Math.evaluate
import nl.xandermarc.mc.lib.Math.secondDerivative
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Vector3d

@Serializable
data class BezierPath(
    @Serializable(with = Vector3dSerializer::class)
    var p0: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    var p1: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    var p2: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    var p3: Vector3d = Vector3d(),
) : Path() {
    override fun getPositionAt(t: Double): Vector3d  = evaluate(p0, p1, p2, p3, t)
    override fun getTangentAt(t: Double): Vector3d = derivative(p0, p1, p2, p3, t).normalize()
    override fun length(): Double = p0.distance(p1) + p1.distance(p2) + p2.distance(p3)
    fun secondDerivativeAt(t: Double): Vector3d = secondDerivative(p0, p1, p2, p3, t)
}
