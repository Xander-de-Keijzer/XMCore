package nl.xandermarc.mc.lib.path

import org.joml.Vector3d

abstract class Path {
    fun positionAt(t: Double): Vector3d = getPositionAt(t.coerceIn(0.0, 1.0))
    fun tangentAt(t: Double): Vector3d = getTangentAt(t.coerceIn(0.0, 1.0))
    abstract fun length(): Double
    protected abstract fun getPositionAt(t: Double): Vector3d
    protected abstract fun getTangentAt(t: Double): Vector3d
}
