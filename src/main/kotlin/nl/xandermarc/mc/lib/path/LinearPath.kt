package nl.xandermarc.mc.lib.path

import org.joml.Vector3d

class LinearPath(
    private val start: Vector3d,
    private val end: Vector3d,
) : Path() {
    override fun getPositionAt(t: Double): Vector3d =
        start.lerp(end, t)

    override fun getForwardAt(t: Double): Vector3d = end.sub(start).normalize()

    override fun length(): Double = start.distance(end)
}
