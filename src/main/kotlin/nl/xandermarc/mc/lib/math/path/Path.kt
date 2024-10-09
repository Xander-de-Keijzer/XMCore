package nl.xandermarc.mc.lib.math.path

import nl.xandermarc.mc.lib.math.Vec

abstract class Path {
    fun positionAt(t: Double): Vec = getPositionAt(t.coerceIn(0.0, 1.0))
    protected abstract fun getPositionAt(t: Double): Vec
}
