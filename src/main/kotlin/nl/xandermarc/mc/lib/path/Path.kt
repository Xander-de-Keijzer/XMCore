package nl.xandermarc.mc.lib.path

import org.joml.Quaterniond
import org.joml.Vector3d

abstract class Path {
    fun positionAt(t: Double): Vector3d = getPositionAt(t.coerceIn(0.0, 1.0))
    fun rotationAt(t: Double): Quaterniond = getRotationAt(t.coerceIn(0.0, 1.0))
    protected abstract fun getPositionAt(t: Double): Vector3d
    protected abstract fun getRotationAt(t: Double): Quaterniond
}
