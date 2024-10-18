package nl.xandermarc.mc.lib.path

import org.joml.Quaterniond
import org.joml.Vector3d

class LinearPath(
    private val start: Vector3d,
    private val end: Vector3d,
) : Path() {
    override fun getPositionAt(t: Double): Vector3d =
        start.lerp(end, t)

    override fun getRotationAt(t: Double): Quaterniond =
        Quaterniond().lookAlong(end.sub(start).normalize(), Vector3d(0.0, 1.0, 0.0))
}

